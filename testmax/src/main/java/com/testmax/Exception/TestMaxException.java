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
package com.testmax.Exception;

public class TestMaxException extends Exception {


    /** holds error string */
    private String message= null;
    /** holds name of exception */
    private String exceptionName = null;
   /** Creates a new instance of ConfigException
    * @param msg - Error message
    */
    public TestMaxException(String msg, String eName) {
        this.message = msg;
        this.exceptionName = eName;
    }
    public TestMaxException(String msg) {
        this.message = msg;
    }
    /**
     * This method return the error message associated with the exception object
     * @return - error message
     */
    public String getMessage(){
        return this.message;
    }
    /**
     * This method sets an error message
     * @param msg - error message
     */
    public void setMessage(String msg){
        this.message = msg;
    }
}
