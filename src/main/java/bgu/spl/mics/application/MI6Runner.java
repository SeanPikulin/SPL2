package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.Inventory;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class MI6Runner {
    public static void main(String[] args) {
        Inventory i=Inventory.getInstance();
        String[] Inventory= {"Sean","Inon","Tali","Matan","Benny","Ori"};
        i.load(Inventory);
        i.printToFile("../Inventory.json");
    }
}
