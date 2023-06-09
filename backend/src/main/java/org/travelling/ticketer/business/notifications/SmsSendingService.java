package org.travelling.ticketer.business.notifications;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.travelling.ticketer.business.DelayMessageProvider;
import org.travelling.ticketer.dto.TicketForNotificationDTO;

import javax.annotation.PostConstruct;

@Service
public class SmsSendingService implements NotificationSender {

    private String sid;

    private String token;

    @Value("${twilio.phone.number.sender}")
    private String senderPhone;

    private final DelayMessageProvider delayMessageProvider;

    @PostConstruct
    public void initialize (){
        sid = System.getenv("twilio_sid");
        token = System.getenv("twilio_token");
    }

    public SmsSendingService(DelayMessageProvider delayMessageProvider) {
        this.delayMessageProvider = delayMessageProvider;
    }

    @PostConstruct
    public void init (){
        Twilio.init(sid, token);
    }

    @Override
    public void send(TicketForNotificationDTO ticketData) {
        Message.creator(
                        new PhoneNumber(ticketData.getRecipientPhoneNumber()),
                        new PhoneNumber(senderPhone),
                        delayMessageProvider.provideMessage(ticketData))
                .create();
    }
}
