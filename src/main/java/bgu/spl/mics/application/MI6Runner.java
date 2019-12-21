package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.Report;

import java.util.List;
import java.util.Vector;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class MI6Runner {
    public static void main(String[] args) {
        Diary i=Diary.getInstance();
        List<String> agentsNumbers1=new Vector<>();
        agentsNumbers1.add("001");
        agentsNumbers1.add("007");
        List<String> agentNames1=new Vector<>();
        agentNames1.add("Sean");
        agentNames1.add("ItayNeria.");

        List<String> agentsNumbers2=new Vector<>();
        agentsNumbers2.add("003");
        agentsNumbers2.add("002");
        List<String> agentNames2=new Vector<>();
        agentNames2.add("Benny");
        agentNames2.add("Adler");

        Report report1=new Report("InonEzraton",1,2,agentsNumbers1,agentNames1,"VisualNovel",10,7,12);
        Report report2=new Report("OriKingBidiuk",3,4,agentsNumbers2,agentNames2,"AmartiLahem",12,3,13);
        i.addReport(report1);
        i.addReport(report2);
        i.incrementTotal();
        i.incrementTotal();
        i.printToFile("../Diary.json");
    }
}
