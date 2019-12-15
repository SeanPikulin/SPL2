package bgu.spl.mics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MessageBrokerTest {
    MessageBroker m;
    Broadcast broadcast;
    Callback<Integer> callback;
    DummyEvent event;
    DummySubscriber s1;
    SimplePublisher s2;
    @BeforeEach
    public void setUp(){
        m= MessageBrokerImpl.getInstance();
        broadcast=new DummyBroadcast();
        callback=new DummyCallback();
        event=new DummyEvent();
        s2=new SimplePublisher();
        s1 = new DummySubscriber(){
            @Override
            protected void initialize() {
                m.register(s1);
                m.subscribeEvent(event.getClass(),s1);
                m.subscribeBroadcast(broadcast.getClass(),s1);
            }
        };

    }


    @Test
    public void testGetInstance(){
        MessageBroker m2=MessageBrokerImpl.getInstance();
        assertTrue(m2==m);
    }

    @Test
    public void testSubscribeEvent(){
//        m.sendEvent(event);
//        Message p= null;
//        try {
//            p = m.awaitMessage(s);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        assertEquals(p,event);//there is a message
        s2.sendEvent(event);

    }

    @Test
    public void testAwaitMessage(){
        m.sendEvent(event);
        Message p= null;
        try {
            p = m.awaitMessage(s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(p,event);//there is an event

        m.sendBroadcast(broadcast);
        Message b= null;
        try {
            b = m.awaitMessage(s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(b,broadcast);//there is a broadcast
    }

    @Test
    public void testSubscribeBroadcast(){
        m.sendBroadcast(broadcast);
        Message b= null;
        try {
            b = m.awaitMessage(s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(b,broadcast);//there is a broadcast
    }

    @Test
    public void testSendEvent() {
        Future<Integer> future = m.sendEvent(event);
        assertNotNull(future);
        m.complete(event, 42);
        assertTrue(future.isDone());
    }

    @Test
    public void testSendBroadcast() {
        m.sendBroadcast(broadcast);
        Message b= null;
        try {
            b = m.awaitMessage(s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(b,broadcast);//there is a broadcast
    }

    @Test
    public void testComplete() {
        Future<Integer> future = m.sendEvent(event);
        m.complete(event, 42);
        assertTrue(future.isDone());
    }
}
