package org.mittman.schedule;

import java.util.Iterator;
import java.util.List;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlResponse;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

public class LongPollingJob extends Job {
	private final int NUM_QUEUES = 5;
	private String queueUrl;


	public LongPollingJob(String name) {
		super(name);
	}

	@Override
	protected void execute() throws Exception {
		SqsClient client = SqsClient.builder()
				.region(Region.US_EAST_2)
				.credentialsProvider(ProfileCredentialsProvider.builder()
						.profileName("Edward1007")
						.build())
				.build();

		initializeQueueUrl(client);

		ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
				.queueUrl(queueUrl)
				.maxNumberOfMessages(1)
				.build();

		List<Message> messages= client.receiveMessage(receiveMessageRequest).messages();

		boolean consumed = false;
		if ( !messages.isEmpty()) {
			consumed = consume(client, messages);
		}
		if ( !consumed) {
			throw new Exception("Did not receive a message");
		}
	}

	private void initializeQueueUrl(SqsClient client) throws Exception {
		int queueNum = Integer.valueOf( getName().substring("Job".length()) ) % NUM_QUEUES;
		if (queueNum==0) {
			queueNum = NUM_QUEUES;
		}
		
		String queueName = "job" + queueNum + "-queue";
		
		System.out.println(getName() + " will poll " + queueName);
		
		GetQueueUrlResponse getQueueUrlResponse =
				client.getQueueUrl(GetQueueUrlRequest.builder()
						.queueName(queueName)
						.build());
		queueUrl = getQueueUrlResponse.queueUrl();
	}

	private boolean consume(SqsClient client, List<Message> messages) throws Exception {
		boolean consumed = false;

		Iterator<Message> iter = messages.iterator();
		while ( !consumed && iter.hasNext()) {
			Message message = iter.next();

			String body = message.body();

			if (body!= null && body.compareTo(getName())==0) {
				System.out.println(getName() + " received message " +
						message.messageId() +
						": " + message.body());
				
				consumed = true;
				
				DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest.builder()
						.queueUrl(queueUrl)
						.receiptHandle(message.receiptHandle())
						.build();

				client.deleteMessage(deleteMessageRequest);
			}
		}
		
		return consumed;
	}

}
