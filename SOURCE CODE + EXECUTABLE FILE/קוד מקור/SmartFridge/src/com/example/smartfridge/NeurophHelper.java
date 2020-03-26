package com.example.smartfridge;

import java.io.InputStream;
//import java.util.HashMap;

//import neuroph.android.example.R;

import org.neuroph.core.NeuralNetwork;

import android.app.Activity;

public class NeurophHelper{

	private NeuralNetwork nnet;
	private Activity act;
	//private org.neuroph.
	
	public NeurophHelper(Activity a){
		act = a;
		loadData();
	}
	
	private void loadData() {
		//showDialog(LOADING_DATA_DIALOG);
                // load neural network in separate thread with stack size = 32000
		new Thread(null, loadDataRunnable, "dataLoader", 32000).start();
	}
	
	private Runnable loadDataRunnable = new Runnable() {
		public void run() {
                        // open neural network
			InputStream is = act.getResources().openRawResource(R.raw.my_nn);
                        // load neural network
			nnet = NeuralNetwork.load(is);
			//imageRecognition = (ImageRecognitionPlugin) nnet.getPlugin(ImageRecognitionPlugin.class);
                        // dismiss loading dialog
			//dismissDialog(LOADING_DATA_DIALOG);
		}
	};
	

	public String recognize() {
		//showDialog(RECOGNIZING_IMAGE_DIALOG);
                // recognize image
		//HashMap<String, Double> output = imageRecognition.recognizeImage(image);
		//dismissDialog(RECOGNIZING_IMAGE_DIALOG);
		
		// set network input 
		nnet.setInput(0,0,1,1,0,1,0,1,0,0); 
		 
		// calculate network 
		nnet.calculate(); 
		 
		// get network output 
		double[] networkOutput = nnet.getOutput(); 

		
		return getAnswer(networkOutput);
	}


	private String getAnswer(double[] networkOutput) {

		String s = "";
		
		for (int i=0; i< networkOutput.length; ++i )
			s+= networkOutput[i] + ", ";
		
		return s;
	}
	
	
	
}
