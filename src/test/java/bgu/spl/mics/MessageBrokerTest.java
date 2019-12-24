package bgu.spl.mics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MessageBrokerTest {
    MessageBroker m;
    Broadcast broadcast;
    DummyEvent event;
    DummySubscriber s1;
    SimplePublisher s2;
    @BeforeEach
    public void setUp(){
        m= MessageBrokerImpl.getInstance();
        broadcast=new DummyBroadcast();
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
        s1.initialize();
        s1.sendEvent(event);//this function calls the m.sendEvent
        try {
            m.awaitMessage(s1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        s1.complete(event,42);
        assertEquals(s1.getResult(), 42);
    }

    @Test
    public void testAwaitMessage(){
        s1.initialize();
        s1.sendEvent(event);
        Message p= null;
        try {
            p = m.awaitMessage(s1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(p,event);//there is an event

        m.sendBroadcast(broadcast);
        Message b= null;
        try {
            b = m.awaitMessage(s1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(b,broadcast);//there is a broadcast
    }

    @Test
    public void testSubscribeBroadcast(){
        s1.initialize();
        m.sendBroadcast(broadcast);
        Message b= null;
        try {
            b = m.awaitMessage(s1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(b,broadcast);//there is a broadcast
    }

    @Test
    public void testSendEvent() {
        s1.initialize();
        Future<Integer> future = m.sendEvent(event);
        assertNotNull(future);
        m.complete(event, 42);
        assertTrue(future.isDone());
    }

    @Test
    public void testSendBroadcast() {
        s1.initialize();
        m.sendBroadcast(broadcast);
        Message b= null;
        try {
            b = m.awaitMessage(s1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(b,broadcast);//there is a broadcast
    }

    @Test
    public void testComplete() {
        s1.initialize();
        Future<Integer> future = m.sendEvent(event);
        m.complete(event, 42);
        assertTrue(future.isDone());
    }
}
