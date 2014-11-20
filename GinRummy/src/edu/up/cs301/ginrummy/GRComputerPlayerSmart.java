package edu.up.cs301.ginrummy;

import java.util.Random;

import android.util.Log;
import edu.up.cs301.card.Card;
import edu.up.cs301.card.Deck;
import edu.up.cs301.card.Rank;
import edu.up.cs301.game.GameComputerPlayer;
import edu.up.cs301.game.infoMsg.GameInfo;
import edu.up.cs301.game.infoMsg.TimerInfo;

/**
 * This is a computer player that slaps at an average rate given
 * by the constructor parameter.
 * 
 * @author Steven R. Vegdahl
 * @version July 2013 
 */
public class GRComputerPlayerSmart extends GameComputerPlayer {	
	public static final int THIS_PLAYER = 1;
	public static final int MELD = 0;
	public static final int ONE_HALF_MELD = 10;
	public static final int TWO_HALF_MELD = 5;
	public static final int SINGLETON = 20;
	
	// the most recent state of the game
	private GRState savedState;
	private Random randDeck;
	private Random randCard;
	
	
    /**
     * Constructor for the SJComputerPlayer class; creates an "average"
     * player.
     * 
     * @param name
     * 		the player's name
     */
    
    /*
     * Constructor for the SJComputerPlayer class
     */
    public GRComputerPlayerSmart(String name) {
        // invoke superclass constructor
        super(name);
        randDeck = new Random();
        randCard = new Random();
        
    }
    
    public Card cardToDiscard(Deck hand){
    	for(Card c : hand.cards){
    		if(c.getRL() >= 3 || c.getSL() >= 3){
    			c.priority = MELD;
    		}else if(c.getRL() ==  1 && c.getSL() == 1){
    			c.priority = SINGLETON;
    		}else if(c.getRL() == 2 && c.getSL() == 2){
    			c.priority = TWO_HALF_MELD;
    		}else if(c.getRL() == 2 || c.getSL() == 2){
    			c.priority = ONE_HALF_MELD;
    		}else{
    			c.priority = 20;
    		}
    		
    		if(c.getRank().value(1) < 10){
    			c.priority *= c.getRank().value(1);
    		}else{
    			c.priority *= 10;
    		}
    	}
    	Card theCard = null;
    	int highest = 0;
    	for(Card c : hand.cards){
    		if(c.priority > highest){
    			highest = c.priority;
    			theCard = c;
    		}
    	}
    	
    	return theCard;
    }
    /**
     * callback method, called when we receive a message, typically from
     * the game
     */
    @Override
    protected void receiveInfo(GameInfo info) {
    	
    	// if we don't have a game-state, ignore
    	if (!(info instanceof GRState)) {
    		return;
    	}
    	
//    	if(savedState != null && savedState.isEndOfRound){
//    		return;
//    	}
    	
    	// update our state variable
    	savedState = (GRState)info;
    	
    	//Check if it's this players turn
    	if(savedState.whoseTurn() == THIS_PLAYER){
    		
    		
    		//DRAW PHASE
    		if(savedState.getPhase() == savedState.DRAW_PHASE){
    			//delay
        		try {
    				Thread.sleep(500);
    			} catch (InterruptedException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    			synchronized(this){
    				savedState.assessMelds(THIS_PLAYER);
    				savedState.canKnock(savedState.getHand(THIS_PLAYER), savedState.getMeldsForPlayer(THIS_PLAYER));

    				Card topOfDiscard = savedState.getDiscard().peekAtTopCard();
    				GRState copy = savedState;

    				copy.getHand(THIS_PLAYER).add(topOfDiscard);
    				copy.assessMelds(THIS_PLAYER);
    				copy.canKnock(copy.getHand(THIS_PLAYER), copy.getMeldsForPlayer(THIS_PLAYER));

    				Card dc = cardToDiscard(copy.getHand(THIS_PLAYER));

    				boolean drawFromStock;
    				if(dc.equals(topOfDiscard)){
    					drawFromStock = true;
    				}else{
    					drawFromStock = false;
    				}

    				game.sendAction(new GRDrawAction(this,drawFromStock));
    			}
    		} 
    		//DISCARD PHASE
    		else if (savedState.getPhase() == savedState.DISCARD_PHASE && !savedState.isEndOfRound){
    			//delay
        		try {
    				Thread.sleep(500);
    			} catch (InterruptedException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    			synchronized(this){
    				GRState s = new GRState(savedState);
    				Card c = cardToDiscard(s.getHand(THIS_PLAYER));
    				s.getHand(THIS_PLAYER).remove(c);
    				
    				s.assessMelds(THIS_PLAYER);
    				s.canKnock(s.getHand(THIS_PLAYER), s.getMeldsForPlayer(THIS_PLAYER));
    				
    				if(s.canKnock(s.getHand(THIS_PLAYER), s.getMeldsForPlayer(THIS_PLAYER))){
    					game.sendAction(new GRKnockAction(this,c));
    				}else{
    					game.sendAction(new GRDiscardAction(this, c));
    				}
    			}
    		}
    	}
    }
}
