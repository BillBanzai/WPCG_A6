package computergraphics.hlsvis.rabbitmq;

/**
 * Callback for RabbitMQ messages.
 * 
 * @author Philipp Jenke
 *
 */
public interface IMessageCallback {

	/**
	 * This method is called when RabbitMQ received a message.
	 */
	public void messageReceived(String message);

}
