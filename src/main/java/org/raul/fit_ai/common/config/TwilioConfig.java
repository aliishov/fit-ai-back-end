package org.raul.fit_ai.common.config;

import com.twilio.Twilio;
import com.twilio.http.TwilioRestClient;
import lombok.RequiredArgsConstructor;
import org.raul.fit_ai.notification.util.TwilioProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class TwilioConfig {

	private final TwilioProperties twilioProperties;

	@Bean
	public TwilioRestClient twilioRestClient() {
		Twilio.init(twilioProperties.getAccountSid(), twilioProperties.getAuthToken());
		return Twilio.getRestClient();
	}
}
