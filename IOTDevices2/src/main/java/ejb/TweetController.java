package ejb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.resource.spi.SecurityException;

import entities.Device;
import entities.Tweet;

/**
 * 
 * @author Alejandro Rodriguez
 * Dat250 course
 *
 *   Tweet Controller class for the management of tweets
 */

@Named(value = "tweetController")
@RequestScoped
public class TweetController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	// Injected DAO EJB:
	@EJB
	private DeviceDao tweetDao;

	private Device tweet;

	public List<Device> getTweets() {
		List<Device> reverseTweetList = new ArrayList<Device>();
		reverseTweetList.addAll(this.tweetDao.getAllDevices());
		Collections.reverse(reverseTweetList);
		return reverseTweetList;

	}

	public String saveTweet() throws NamingException, JMSException {
		SessionUtils.getUserName();
		this.tweet.setAuthor(SessionUtils.getUserName());
		this.tweetDao.persist(this.tweet);
		return Constants.INDEX;
	}

	public Device getTweet() {
		if (this.tweet == null) {
			tweet = new Device();
		}
		return tweet;

	}

}
