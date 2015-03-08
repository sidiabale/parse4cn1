/**
 * Your application code goes here
 */

package userclasses;

import generated.StateMachineBase;
import com.codename1.ui.*; 
import com.codename1.ui.events.*;
import com.codename1.ui.util.Resources;
import com.parse4cn1.Parse;
import com.parse4cn1.ParseException;
import com.parse4cn1.command.ParseGetCommand;
import com.parse4cn1.command.ParseResponse;

/**
 *
 * @author Your name here
 */
public class StateMachine extends StateMachineBase {
    public StateMachine(String resFile) {
        super(resFile);
        // do not modify, write code in initVars and initialize class members there,
        // the constructor might be invoked too late due to race conditions that might occur
    }
    
    /**
     * this method should be used to initialize variables instead of
     * the constructor/class scope to avoid race conditions
     */
    protected void initVars(Resources res) {
        Parse.initialize("j1KMuH9otZlHcPncU9dZ1JFH7cXL8K5XUiQQ9ot8", "pW7IhlgwwB2WgmvK1yYguSaUgTofjCmyOX6vUh8k");
        ParseGetCommand command = new ParseGetCommand("users", "nonExistentUserID");
        try {
            ParseResponse response = command.perform();
        } catch (ParseException ex) {
          System.out.println(ex);
        }
    }

}
