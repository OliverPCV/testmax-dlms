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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;


public class WmLog {

    // string to control layout of loggers
    private static final String LOG4J_LAYOUT_PATTERN = "%p %d{ABSOLUTE} %c - %m%n";
    
    /** constant - allowed log levels */
    public static final String [] LOG_LEVELS = {"ALL", "TRACE", "DEBUG", "INFO", "WARN", "ERROR", "FATAL", "OFF"};
    
    //log default is INFO.
    private static Level WM_LOG_LEVEL = Level.ALL;  
    
    // Logger for WM Engine
    private static Logger coreLogger = null;
    /** Add a file appender to the logger
    *
    * @param logname log file full path
    * @param logLevel log level
    * @param l the target logger
    */
   private static void initializeLogger(String logname, Level logLevel, Logger l){
	  
       PatternLayout layout = new PatternLayout();
       layout.setConversionPattern( LOG4J_LAYOUT_PATTERN );
       FileAppender appender = null;
       
       try {
           appender = new FileAppender(layout, logname, false);
       } catch(Exception e) {
           coreLogger.error("TestMaxLog.initializeLogger - Could not initialize logger for " + logname);
       }
       
       l.addAppender(appender);
       l.setLevel(logLevel);
       
      
   }
   /**
    * Add a file appender to the logger with defautl level.
    * 
    * @param logname log file full path
    * @param l the target logger
    */
   public static void initializeLogger(String logname, Logger l){
       initializeLogger(logname, WM_LOG_LEVEL, l);
   
   }
   
   /**
    * add a file appender to core logger
    * @param logdir log directory
    */
   public static synchronized void initCoreLogger(String logdir ,String loggerName) {
	   coreLogger = Logger.getLogger(loggerName);
       System.out.println(logdir);
       initializeLogger(logdir, coreLogger);
   
   }
   
   
   /**
    * get core logger
    * @return core logger
    */
   public static synchronized Logger getCoreLogger() {
       return coreLogger;
   }
   public static synchronized void closeLogger() {
	   coreLogger.shutdown();
   }
   
   public static synchronized void printMessage(String msg){
	   	System.out.println(msg);
		WmLog.getCoreLogger().info(msg);	
		 
	}
   
   public static String getStackTrace(Throwable aThrowable) {
	    final Writer result = new StringWriter();
	    final PrintWriter printWriter = new PrintWriter(result);
	    aThrowable.printStackTrace(printWriter);
	    return result.toString();
	  }
}
