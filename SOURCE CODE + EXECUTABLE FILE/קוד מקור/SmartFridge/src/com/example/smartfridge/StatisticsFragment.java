package com.example.smartfridge;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;

import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.jjoe64.graphview.LineGraphView;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class StatisticsFragment extends Fragment {
	

	List<ParseObject> mCategoryList;
	ProgressBar mProgressBar;
	View mView;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_statistics, container,
				false);
		mView = view;
		//mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
		
//		setGraphByCategory(view);
		
		//new GetDataTask().execute();
		mCategoryList = null;

		ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Category");
		query1.addAscendingOrder("order");
		query1.whereExists("category_name");

		try {
			StatisticsFragment.this.mCategoryList = query1.find();
			setGraphByWeeks(mView);
		}
		catch(Exception e)
		{}
		
		return view;
	}
	
	public void setGraphByMonths(View view){
		/*
		 * create graph
		 */
		final LineGraphView graphView = new LineGraphView(
		      this.getActivity()
		      , "ProductByMonths"
		);
		
		final Date curDate = new Date();
		List<Thread> threads = new ArrayList<Thread>();
		
		List<GraphViewSeriesStyle> styles = new ArrayList<GraphViewSeriesStyle>();
		final List<GraphViewSeries> seriess = new ArrayList<GraphViewSeries>();
		final List<GraphViewData[]> datas = new ArrayList<GraphViewData[]>();
		for (int i = 0; i < mCategoryList.size(); i++) {
			styles.add(null);
			seriess.add(null);
			datas.add(null);
			}

		for (int i = 0; i < mCategoryList.size(); ++i) {
			final int tmpIndex = i;
			Thread tmpThread = new Thread(new Runnable() {
				@Override
				public void run() {
					HashMap<Integer, Integer> myMap = new HashMap<Integer, Integer>();
					ParseQuery<ParseObject> innerQuery = ParseQuery
							.getQuery("Product");
					innerQuery.whereEqualTo("category",
							mCategoryList.get(tmpIndex));
					ParseQuery<ParseObject> query = ParseQuery
							.getQuery("ProductHistory");
					query.whereEqualTo("user", ParseUser.getCurrentUser());
					query.include("product");
					query.whereMatchesQuery("product", innerQuery);
					query.addDescendingOrder("updatedAt");
					query.setLimit(60);
					try {
						List<ParseObject> PHList = query.find();
						for (ParseObject PHItem : PHList) {
							Date itemDate = PHItem.getUpdatedAt();
							int itemYear = itemDate.getYear();
							int curYear = itemDate.getYear();
							if (itemYear == curYear){
								if( myMap.get(itemDate.getMonth())  == null){
									myMap.put(itemDate.getMonth(), PHItem.getInt("wasBuy"));
								}
								else{
									int tmpInt = myMap.get(itemDate.getMonth());
									myMap.remove(itemDate.getMonth());
									myMap.put(itemDate.getMonth(), PHItem.getInt("wasBuy") + tmpInt);
								}
							}
						}
						
					datas.set(tmpIndex, new GraphViewData[myMap.size()] );
					int tmpDataIndx = 0;
//					for (Entry<Integer, Integer> entry : myMap.entrySet()){
//						datas.get(tmpIndex)[tmpDataIndx] =  new GraphViewData(entry.getKey(), entry.getValue());
//						++tmpDataIndx;
//					}
					SortedSet<Integer> keys = new TreeSet<Integer>(myMap.keySet());
					for (Integer key : keys) { 
						Integer value = myMap.get(key);
					   // do something
						datas.get(tmpIndex)[tmpDataIndx] =  new GraphViewData(key, value);
						++tmpDataIndx;
					}
					tmpDataIndx = 0;
//					GraphViewSeriesStyle style = new GraphViewSeriesStyle();
//					style.color = Color.rgb(90, 250, 00);
					seriess.set(tmpIndex, new GraphViewSeries(mCategoryList.get(tmpIndex).getString("name"), null, datas.get(tmpIndex)) );
						// add data;
					if (datas.get(tmpIndex).length != 0)
					{
						graphView.addSeries(seriess.get(tmpIndex));
					}
						
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			});

			threads.add(tmpThread);
			tmpThread.start();

		}
		

		try {
			for (int i = 0; i < threads.size(); ++i) {
					threads.get(i).join();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// optional - set view port, start=2, size=10
		graphView.setViewPort(1, 12);
		graphView.setScalable(true);
		// optional - legend
		graphView.setShowLegend(true);
		 
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.graph);
		layout.addView(graphView);	

	}
	
	public void setGraphByWeeks(View view){
		
		/*
		 * create graph
		 */
		final LineGraphView graphView = new LineGraphView(
		      this.getActivity()
		      , "ProductByWeeks"
		);
		
		final Date curDate = new Date();
		List<Thread> threads = new ArrayList<Thread>();
		
		List<GraphViewSeriesStyle> styles = new ArrayList<GraphViewSeriesStyle>();
		final List<GraphViewSeries> seriess = new ArrayList<GraphViewSeries>();
		final List<GraphViewData[]> datas = new ArrayList<GraphViewData[]>();
		for (int i = 0; i < mCategoryList.size(); i++) {
			styles.add(null);
			seriess.add(null);
			datas.add(null);
			}

		for (int i = 0; i < mCategoryList.size(); ++i) {
			final int tmpIndex = i;
			Thread tmpThread = new Thread(new Runnable() {
				@Override
				public void run() {
					HashMap<Integer, Integer> myMap = new HashMap<Integer, Integer>();
					ParseQuery<ParseObject> innerQuery = ParseQuery
							.getQuery("Product");
					innerQuery.whereEqualTo("category",
							mCategoryList.get(tmpIndex));
					ParseQuery<ParseObject> query = ParseQuery
							.getQuery("ProductHistory");
					query.whereEqualTo("user", ParseUser.getCurrentUser());
					query.include("product");
					query.whereMatchesQuery("product", innerQuery);
					query.addDescendingOrder("updatedAt");
					query.setLimit(5);
					try {
						List<ParseObject> PHList = query.find();
						for (ParseObject PHItem : PHList) {
							Date itemDate = PHItem.getUpdatedAt();
							int itemMonth = itemDate.getMonth();
							int curMonth = itemDate.getMonth();
							if (itemMonth == curMonth){
								if( myMap.get(PHItem.getInt("day"))  == null){
									myMap.put(PHItem.getInt("day"), PHItem.getInt("wasBuy"));
								}
								else{
									int tmpInt = myMap.get(PHItem.getInt("day"));
									myMap.remove(PHItem.getInt("day"));
									myMap.put(PHItem.getInt("day"), PHItem.getInt("wasBuy") + tmpInt);
								}
							}
						}
						
					datas.set(tmpIndex, new GraphViewData[myMap.size()] );
					int tmpDataIndx = 0;
//					for (Entry<Integer, Integer> entry : myMap.entrySet()){
//						datas.get(tmpIndex)[tmpDataIndx] =  new GraphViewData(entry.getKey(), entry.getValue());
//						++tmpDataIndx;
//					}
					SortedSet<Integer> keys = new TreeSet<Integer>(myMap.keySet());
					for (Integer key : keys) { 
						Integer value = myMap.get(key);
					   // do something
						datas.get(tmpIndex)[tmpDataIndx] =  new GraphViewData(key, value);
						++tmpDataIndx;
					}
					tmpDataIndx = 0;
//					GraphViewSeriesStyle style = new GraphViewSeriesStyle();
//					style.color = Color.rgb(90, 250, 00);
					seriess.set(tmpIndex, new GraphViewSeries(mCategoryList.get(tmpIndex).getString("name"), null, datas.get(tmpIndex)) );
						// add data;
					if (datas.get(tmpIndex).length != 0)
					{
						graphView.addSeries(seriess.get(tmpIndex));
					}
						
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			});

			threads.add(tmpThread);
			tmpThread.start();

		}
		

		try {
			for (int i = 0; i < threads.size(); ++i) {
					threads.get(i).join();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// optional - set view port, start=2, size=10
		graphView.setViewPort(1, 30);
		graphView.setScalable(true);
		// optional - legend
		graphView.setShowLegend(true);
		 
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.graph);
		layout.addView(graphView);	
	}
	
	public void example(View view)
	{
		// first init data
		// sin curve
		int num = 150;
		GraphViewData[] data = new GraphViewData[num];
		double v=0;
		for (int i=0; i<num; i++) {
		   v += 0.2;
		   data[i] = new GraphViewData(i, Math.sin(v));
		}
		GraphViewSeriesStyle styleSin = new GraphViewSeriesStyle();
		styleSin.color = Color.rgb(200, 50, 00);
		GraphViewSeries seriesSin = new GraphViewSeries("Sinus curve", styleSin, data);
		 
		// cos curve
		data = new GraphViewData[num];
		v=0;
		for (int i=0; i<num; i++) {
		   v += 0.2;
		   data[i] = new GraphViewData(i, Math.cos(v));
		}
		GraphViewSeriesStyle styleCos = new GraphViewSeriesStyle();
		styleCos.color = Color.rgb(90, 250, 00);
		GraphViewSeries seriesCos = new GraphViewSeries("Cosinus curve", styleCos, data);
		 
		// random curve
		num = 1000;
		data = new GraphViewData[num];
		v=0;
		for (int i=0; i<num; i++) {
		   v += 0.2;
		   data[i] = new GraphViewData(i, Math.sin(Math.random()*v));
		}
		GraphViewSeries seriesRnd = new GraphViewSeries("Random curve", null, data);
		 
		/*
		 * create graph
		 */
		LineGraphView graphView = new LineGraphView(
		      this.getActivity()
		      , "GraphViewDemo"
		);
		// add data
		graphView.addSeries(seriesCos);
		graphView.addSeries(seriesSin);
		graphView.addSeries(seriesRnd);
		// optional - set view port, start=2, size=10
		graphView.setViewPort(2, 10);
		graphView.setScalable(true);
		// optional - legend
		graphView.setShowLegend(true);

		LinearLayout layout = (LinearLayout) view.findViewById(R.id.graph);
		layout.addView(graphView);	
		
	}

//	private class GetDataTask extends AsyncTask<Void, Void, String[]> {
//		@Override
//		protected void onPostExecute(String[] result) {
//			mProgressBar.setVisibility(View.GONE);
//		}
//
//		@Override
//		protected String[] doInBackground(Void... arg0) {
//
//			mCategoryList = null;
//
//			ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Category");
//			query1.addAscendingOrder("order");
//			query1.whereExists("category_name");
//
//			try {
//				StatisticsFragment.this.mCategoryList = query1.find();
//				// ----------------------
//				aaa();
//				// ----------------------
//			} catch (ParseException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//			return null;
//		}
//	}
	
	
	public void setDefaultLocale(Context context, Locale locale) {
        Locale.setDefault(locale);
        Configuration appConfig = new Configuration();
        appConfig.locale = locale;
        context.getResources().updateConfiguration(appConfig,
                context.getResources().getDisplayMetrics());
        System.out.println("trad" + locale.getLanguage());
    } 
	
}
