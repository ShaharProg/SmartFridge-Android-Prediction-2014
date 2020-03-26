
// Use Parse.Cloud.define to define as many cloud functions as you want.
// For example:
Parse.Cloud.define("hello", function(request, response) {
	response.success("Hello world!");
});

//update average rating
Parse.Cloud.afterSave("RecipeRating", function(request) {
  query = new Parse.Query("RecipeRating");
  query.equalTo("recipe", request.object.get("recipe"));
  query.find({
		success: function(results){
			var n = results.length;
			var sum = 0;
			
			for (var i=0; i<n; ++i){
				sum += results[i].get("rating");
			}
			
			var avg = sum/n;
			query2 = new Parse.Query("Recipe");
			query2.get(request.object.get("recipe").id, {
				success: function(recipe) {
				  recipe.set("rating",avg);
				  recipe.save();
				},
				error: function(error) {
				  console.error("Got an error " + error.code + " : " + error.message);
				}
			});
			
		},
		error: function(error){
		}
	});
});

//Linear Regression
Parse.Cloud.define("linearRegression", function(request, response) {
	//console.log("Start func linearRegression.");
	
	var query = new Parse.Query("ProductHistory");
	query.equalTo("userId",request.params.user_id);
	query.equalTo("productId",request.params.product_id);
	//console.log("userId: " + request.params.user_id);
	//console.log("productId: " + request.params.product_id);
	query.descending("createdAt");
	query.limit(6);
	query.find({
		success: function(results){
			
			var n = results.length;
			//console.log("n: " + n);
			if (n >= 4){
				var sumX = 0.0;
				var sumX2 = 0.0;
				var sumY = 0.0;
				var avgX = 0.0;
				var avgY = 0.0;
				var sumXY = 0.0;

				for(var i=0; i<n-1; ++i){
					sumX += results[i].get("wasInFridge");
					sumX2 += results[i].get("wasInFridge") * results[i].get("wasInFridge");
					sumY += results[i].get("wasBuy");
					sumXY += results[i].get("wasInFridge") * results[i].get("wasBuy");
				}

				avgX = sumX / n;
				avgY = sumY / n;

				var b = (n*sumXY - sumX*sumY) / (n*sumX2 - sumX*sumX);
				var a = avgY - b*avgX;

				var resultPredicition = Math.round(a-b*results[n-1].get("wasInFridge"));
				response.success(resultPredicition);
			}
			else{
				//console.log("Too few history");
			}
		},
		error: function(error) {
            response.error("Got an error " + error.code + " : " + error.message);
        }
	});
	//console.log("End func linearRegression.");
});

// Per user shop list prediction
Parse.Cloud.define("perUserPrediction", function(request, response) {
	//console.log("Start func perUserPrediction.");

	//console.log("request.params.user_id: " + request.params.user_id);
	var userQuery = new Parse.Query(Parse.User);
	userQuery.get(request.params.user_id, {
		success: function(user) {
			//console.log("user: " + user.id);
			var query = new Parse.Query("UserProducts");
			query.equalTo("user", user);
			query.include("product");
			query.find({
				success: function(userProducts) {
						//console.log("zzzzzzzzzzzzzzzzz: " + userProducts.length);
					for (var i=0; i<userProducts.length; ++i){
						var productId = userProducts[i].get("product").id;
						var product = userProducts[i].get("product");
						//add the current InFridge state
						var IFQuery = new Parse.Query("InFridge");
						IFQuery.equalTo("user", user);
						IFQuery.equalTo("product", product);
						IFQuery.find({
							success: function(curInFridge) {
								
								var ProductHistory = Parse.Object.extend("ProductHistory");
								var newPH = new ProductHistory();
								newPH.set("user", user);
								newPH.set("userId", user.id);
								newPH.set("product", product);
								newPH.set("productId", productId);
								if (curInFridge.length == 0){
									newPH.set("wasInFridge", 0);
								}
								else{
									newPH.set("wasInFridge", curInFridge[0].get("quantity"));
								}
								newPH.set("wasBuy", 0);
								newPH.save(null, {
								  success: function(newPH) {
									// Execute any logic that should take place after the object is saved.
									alert('New object created with objectId: ' + newPH.id);
								  },
								  error: function(newPH, error) {
									// Execute any logic that should take place if the save fails.
									// error is a Parse.Error with an error code and description.
									alert('Failed to create new object, with error code: ' + error.description);
								  }
								});
							},
							error: function(error) {
								response.error("Got an error " + error.code + " : " + error.message);
							}
						});
						
						//calculate
						Parse.Cloud.run('linearRegression', {user_id: request.params.user_id, product_id: productId }, {
							success: function(result){
								// success
									//console.log("sssssssssssssssssssssss");
									//console.log("user: " + user.id);
									//console.log("product: " + productId);
								var shopQuery = new Parse.Query("ShopList");
								shopQuery.equalTo("user", user);
								shopQuery.equalTo("product", product);
								shopQuery.find({
									success: function(results){
											//console.log("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
										if ((results == null) || (results.length == 0))
										{
											var ShopList = Parse.Object.extend("ShopList");
											var shopProduct = new ShopList();
											shopProduct.set("quantity", result);
											shopProduct.set("user", user);
											shopProduct.set("product", product);
												//console.log("quantity: " + result + ",user: " + user + ",product: " + productId);
											shopProduct.save(null, {
											  success: function(shopProduct) {
												// Execute any logic that should take place after the object is saved.
												alert('New object created with objectId: ' + shopProduct.id);
											  },
											  error: function(shopProduct, error) {
												// Execute any logic that should take place if the save fails.
												// error is a Parse.Error with an error code and description.
												alert('Failed to create new object, with error code: ' + error.description);
											  }
											});
										}
										else
										{
											//console.log("results[0]: " + results[0].id);
											var quantity = results[0].get("quantity");
											if (quantity < result)
											{
												results[0].set("quantity", result);
												results[0].save();
											}
										}
										response.success("linearRegression success for user:" + request.params.user_id + ", product: " + productId + ", result: " + result);
									},
									error: function(error) {
										response.error("Got an error " + error.code + " : " + error.message);
									}
								});
							},
							error: function(error){
								// error
								response.error("Got an error " + error.code + " : " + error.message);
							}
						});
					}
				},
				error: function(error){
					response.error("Got an error " + error.code + " : " + error.message);
				}
			});
		},
		error: function(error) {
			response.error("perUserPrediction (query.get(user)) Got an error " + error.code + " : " + error.message);
		}
	});	
	//response.success();
	//console.log("End func perUserPrediction.");
});

//Daily Prediction
Parse.Cloud.job("dailyPrediction", function(request, status) {
	//console.log("Start job dailyPrediction.");
	// Set up to modify user data
	Parse.Cloud.useMasterKey();
	// Query for all users
	var queryUsr = new Parse.Query(Parse.User);
	queryUsr.each( function(curUser) {
		//console.log("curUser: " + curUser.get("username") + ", shoppingDay: " + curUser.get("shoppingDay"));
		var d = new Date();
		var p = curUser.get("shoppingDay");
		//console.log("Date.getDay(): " + d.getDay());
		//console.log("curUser.get('objectId'): " + curUser.id);
		if( d.getDay() == (p-2) ){
			Parse.Cloud.run('perUserPrediction', {user_id: curUser.id}, {
				success: function(){
					// success
					status.message("perUserPrediction success for user:" + curUser.id);
					var query = new Parse.Query(Parse.Installation);
					query.equalTo('user', curUser);
					Parse.Push.send({
					  where: query, // Set our Installation query
					  data: {
						alert: "SmartFridge envisioned a shopping list for you based on past data, which merged with the existing shopping list."
					  }
					}, {
					  success: function() {
						// Push was successful
						response.success("Push was successful.");
					  },
					  error: function(error) {
						// Handle error
						response.error("Push was faild.");
					  }
					});
				},
				error: function(error){
					// error
					status.message("Got an error " + error.code + " : " + error.message);
				}
			});
		}
	}).then(function() {
		// Set the job's success status
		status.success("dailyPrediction completed successfully.");
	}, function(error) {
		// Set the job's error status
		status.error("Uh oh, something went wrong at dailyPrediction.");
	});
	//console.log("End job dailyPrediction.");
});
