package tld.opuni.assignments;

import java.util.Scanner;
import java.util.InputMismatchException;

class Assignment11b extends Assignment {

    @Override public void interact(final App app) {
        System.out.println("Welcome to paper cup folding program!");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("How many cups do you want to fold?");
            int numCaps;
            try {
                numCaps = scanner.nextInt();
            } catch (InputMismatchException xpt) {
                app.quitOrReload();
                scanner = new Scanner(System.in);
                continue;
            }
            double shekels = numCaps * 2.5;
            int time = numCaps * 5;
            int hours = time / 60;
            int minutes = time % 60;
            String hMessage;
            String mMessage;
            String connector;
            if (hours > 0) {
                if (hours % 10 == 1) hMessage = "%d hour";
                else hMessage = "%d hours";
                hMessage = String.format(hMessage, hours);
            } else hMessage = "";
            if (minutes > 0) {
                if (minutes % 10 == 1) mMessage = "%d minute";
                else mMessage = "%d minutes";
                mMessage = String.format(mMessage, minutes);
            } else mMessage = "";
            if (mMessage.length() > 0 && hMessage.length() > 0)
                connector = " and ";
            else connector = "";
            System.out.format("You earned %.2f shekels in just %s%s%s!%n",
                              shekels, hMessage, connector, mMessage);
        }
    }
}
