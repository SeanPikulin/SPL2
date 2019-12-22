package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.publishers.TimeService;
import bgu.spl.mics.application.subscribers.Intelligence;
import bgu.spl.mics.application.subscribers.M;
import bgu.spl.mics.application.subscribers.Moneypenny;
import bgu.spl.mics.application.subscribers.Q;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Vector;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class MI6Runner {
    public static void main(String[] args) {
//        Diary i=Diary.getInstance();
//        List<String> agentsNumbers1=new Vector<>();
//        agentsNumbers1.add("001");
//        agentsNumbers1.add("007");
//        List<String> agentNames1=new Vector<>();
//        agentNames1.add("Sean");
//        agentNames1.add("ItayNeria.");
//
//        List<String> agentsNumbers2=new Vector<>();
//        agentsNumbers2.add("003");
//        agentsNumbers2.add("002");
//        List<String> agentNames2=new Vector<>();
//        agentNames2.add("Benny");
//        agentNames2.add("Adler");
//
//        Report report1=new Report("InonEzraton",1,2,agentsNumbers1,agentNames1,"VisualNovel",10,7,12);
//        Report report2=new Report("OriKingBidiuk",3,4,agentsNumbers2,agentNames2,"AmartiLahem",12,3,13);
//        i.addReport(report1);
//        i.addReport(report2);
//        i.incrementTotal();
//        i.incrementTotal();
//        i.printToFile("../Diary.json");
        Inventory inventory=Inventory.getInstance();
        Squad squad=Squad.getInstance();
        List<Thread> threads=new Vector<>();
        JSONParser parser=new JSONParser();
        try {
            Reader reader=new FileReader(args[0]);
            JSONObject obj=(JSONObject)parser.parse(reader);
            JSONArray inventoryJson = (JSONArray) obj.get("inventory");
            String[] gadgets = new String[inventoryJson.size()];
            for (int i = 0; i < inventoryJson.size(); i++) {
                gadgets[i] = (String) inventoryJson.get(i);
            }
            inventory.load(gadgets);
            JSONArray JSONAgents=(JSONArray)obj.get("squad");
            Agent[] agents=new Agent[JSONAgents.size()];
            for (int i=0;i<JSONAgents.size();i++){
                agents[i]=new Agent();
                agents[i].setName((String)((JSONObject)JSONAgents.get(i)).get("name"));
                agents[i].setSerialNumber((String)((JSONObject)JSONAgents.get(i)).get("serialNumber"));
            }
            squad.load(agents);
            JSONObject services=(JSONObject)obj.get("services");
            M[] MArray=new M[(int)(long)services.get("M")];
            for(int i=0;i<MArray.length;i++){
                MArray[i]=new M(i,(int)(long)services.get("time"));
                Thread thread=new Thread(MArray[i]);
                threads.add(thread);
                thread.start();
            }
            Moneypenny[] moneypennies=new Moneypenny[(int)(long)services.get("Moneypenny")];
            for(int i=0;i<moneypennies.length;i++){
                moneypennies[i]=new Moneypenny(i,(int)(long)services.get("time"));
                Thread thread=new Thread(moneypennies[i]);
                threads.add(thread);
                thread.start();
            }
            JSONArray JSONIntelligence=(JSONArray)services.get("intelligence");
            Intelligence[] intelligences=new Intelligence[JSONIntelligence.size()];
            for(int i=0;i<JSONIntelligence.size();i++){
                JSONObject JSONintelligence=(JSONObject)JSONIntelligence.get(i);
                JSONArray missions=(JSONArray)JSONintelligence.get("missions");
                List<MissionInfo> missionInfos=new Vector<>();
                for(int j=0;j<missions.size();j++){
                    JSONObject mission=(JSONObject)missions.get(j);
                    JSONArray serialNumbers= (JSONArray) mission.get("serialAgentsNumbers");
                    List<String> serialAgentsNumbers=new Vector<>();
                    for(int k=0;k<serialNumbers.size();k++){
                        serialAgentsNumbers.add((String)serialNumbers.get(k));
                    }
                    int duration= (int)(long) mission.get("duration");
                    String gadget= (String) mission.get("gadget");
                    String name= (String) mission.get("name");
                    int timeExpired= (int)(long) mission.get("timeExpired");
                    int timeIssued= (int)(long) mission.get("timeIssued");
                    MissionInfo missionInfo=new MissionInfo(name,serialAgentsNumbers,gadget,timeIssued,timeExpired,duration);
                    missionInfos.add(missionInfo);
                }
                Intelligence intelligence =new Intelligence(missionInfos,(int)(long)services.get("time"));
                intelligences[i] = intelligence;
                Thread thread=new Thread(intelligences[i]);
                threads.add(thread);
                thread.start();
            }
            Q q=new Q((int)(long) services.get("time"));
            Thread thread=new Thread(q);
            threads.add(thread);
            thread.start();

            TimeService timeService=new TimeService((int)(long) services.get("time"));
            Thread threadi=new Thread(timeService);
            threads.add(threadi);
            threadi.start();

            threads.forEach(threadie -> {
                try {
                    threadie.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            inventory.printToFile(args[1]);
            MArray[0].getDiary().printToFile(args[2]);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
