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
package com.testmax.handler;


import com.testmax.framework.BasePage;

public class ApiHandler extends BasePage {

	
	public ApiHandler(){
        super();
       
    }
	
	@Override
    public void execute() {        
       executeTest(this);
       
    }

	@Override
    protected String executeHttpPOST() {
		String resp=null;
        // TODO Auto-generated method stub
        try {
        	resp=request.handleHTTPPost();
           
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return(resp);
        
    }

    @Override
    protected String executeHttpsPOST() {
    	String resp=null;
        // TODO Auto-generated method stub
        try {
        	resp=request.handleHTTPPost();
           
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return(resp);
        
    }

    @Override
    protected String executeHttpGET() {
    	String resp=null;
        try {
        	resp=request.handleHTTPGet();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return(resp);
    }

    @Override
    protected String executeHttpsGET() {
    	String resp=null;
        try {
        	resp=request.handleHTTPGet();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // TODO Auto-generated method stub
        return(resp);
    }
    

	@Override
	protected void executeAPI(ActionHandler proc) {
		try {
			this.proc=proc;
            this.proc.handleProc();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		
	}

}
