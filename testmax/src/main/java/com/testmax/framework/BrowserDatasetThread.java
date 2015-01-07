/*
 * Copyright (C) 2014 Artitelly Inc,
 *
 * Licensed under the Common Public Attribution License Version 1.0 (CPAL-1.0) (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://opensource.org/licenses/CPAL-1.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.testmax.framework;

import java.util.ArrayList;
import com.testmax.handler.ActionHandler;
import com.testmax.handler.JunitHandler;

public class BrowserDatasetThread implements Runnable {

    private ArrayList<JunitHandler> junitHandlers=null;
    
    private Thread thisThread=null;
    
    private long starttime;
    
    private ActionHandler proc=null;
    
    public BrowserDatasetThread(ActionHandler proc, ArrayList<JunitHandler> junitHandlers){
        this.junitHandlers=junitHandlers;
        this.proc=proc;
       
        this.thisThread=new Thread(this);
        this.starttime=System.currentTimeMillis();
       
      
    }
    public void run() {
    	this.proc.junitAssertCounter=0;
        // TODO Auto-generated method stub  
    	for(JunitHandler handler:junitHandlers){
    		this.proc.printMessage("############### STARTED THREAD with Thread="+handler.getThreadIndex());
    		this.proc.printMessage(" ");
    		this.proc.printMessage("############### EXECUTING with Dataset="+handler.getVarMapByThreadIndex(handler.getThreadIndex()).values());
    		//synchronized (handler){    			
    			handler.handleService();
    		//}
    		this.proc.executeJunitAssertData(handler);
    		
    		this.proc.printMessage("############### END OF THREAD="+handler.getThreadIndex());
    	}
       
        try {
        	if(this.proc.junitAssertCounter==junitHandlers.size()){
        		this.thisThread.interrupt();
        		
        		this.thisThread=null;
        		this.junitHandlers=null;
        	}else{
        		this.thisThread.join(); 
        		
        	}
        	
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
       

    }
    
    

}
