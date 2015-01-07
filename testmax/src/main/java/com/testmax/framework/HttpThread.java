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

public class HttpThread implements Runnable {

    private BasePage basePage=null;
    
    private Thread thisThread=null;
    
    private long starttime=0;
    
    private String action=null;
    
    
    public HttpThread(BasePage page, String action){
        this.basePage=page;        
        this.thisThread=new Thread(this);
        this.starttime=System.currentTimeMillis();
        this.action=action;
    }
    
    public void run() {
        // TODO Auto-generated method stub    	
        this.basePage.invoke(this.action);  
       
        try {
        	long timer=(System.currentTimeMillis()-this.starttime)/1000;
        	if(timer>=this.basePage.timeout){
        		this.thisThread.interrupt();
        		this.thisThread=null;
        	}else{
        		this.thisThread.join(); 
        	}
        	
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    
    

}
