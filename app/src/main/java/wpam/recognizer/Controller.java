package wpam.recognizer;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class Controller 
{
	private boolean started;
	
	private RecordTask recordTask;	
	private RecognizerTask recognizerTask;	
	private MainActivity mainActivity;
	BlockingQueue<DataBlock> blockingQueue;

	private Character lastValue;
		
	public Controller(MainActivity mainActivity)
	{
		this.mainActivity = mainActivity;
	}

	public void changeState() 
	{
		if (started == false)
		{
			
			lastValue = ' ';
			
			blockingQueue = new LinkedBlockingQueue<DataBlock>();
			
			mainActivity.start();
			
			recordTask = new RecordTask(this,blockingQueue);
			
			recognizerTask = new RecognizerTask(this,blockingQueue);
			started = true;


			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				recordTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				recognizerTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

			} else {
				recordTask.execute();

				recognizerTask.execute();
			}

			Log.d("ANish","Started------");

		} else {
			
			mainActivity.stop();
			Log.d("ANish","CLOSED------");
			recognizerTask.cancel(true);
			recordTask.cancel(true);

			started = false;
		}
	}

	public void clear() {
		mainActivity.clearText();
	}

	public boolean isStarted() {
		return started;
	}


	public int getAudioSource()
	{
		return mainActivity.getAudioSource();
	}
	
	public void spectrumReady(Spectrum spectrum) 
	{
		mainActivity.drawSpectrum(spectrum);
	}

	public void keyReady(char key) 
	{
		mainActivity.setAciveKey(key);
		
		if(key != ' ')
			if(lastValue != key)
				mainActivity.addText(key);
		
		lastValue = key;
	}
	
	public void debug(String text) 
	{
		mainActivity.setText(text);
	}
}
