import java.util.*;

/**
 * Created by Giampiero on 9/19/2016.
 */
public class CALC2 {

    //private static ArrayList<HashMap<String, Integer>> lstVars = new ArrayList<>();
    private static Map<String, Double> lstVars = new HashMap<>();
    private static Scanner in;
    public static void main(String[] args){
        System.out.println("You must put a space between all \"sqrt\" and values or variables.");
        System.out.println("$>READY FOR INPUT");

        String command = "";
        while (true){
            System.out.print("$>");
            in = new Scanner(System.in);
            command = in.nextLine();
            if (command.contains("stop")){
                break;
            }
            ProcessCommand(command);
            in.reset();
        }
        in.close();
        System.exit(1);

    }

    private static void ProcessCommand(String command) {
        if(command.isEmpty()){
            //System.out.println();
            return;
        }
        String s = "";
        if(command.contains("//")) {
            s = command.substring(0, command.indexOf("//"));
        }
        System.out.println(s);
        String temp;
        String firstCommand = "";
        String restOfCommand = "";
        in = new Scanner(command).useDelimiter(" ");
        firstCommand = in.next().trim();
        if(firstCommand.length() > 0){
            temp = in.nextLine().trim();
            if(temp.contains("//")){
                // String s = temp.substring(0, temp.indexOf("//"));
                String[] splitter = temp.split("//");
                restOfCommand = splitter[0];
            } else {
                restOfCommand = temp;
            }
            in.reset();
            switch(firstCommand){
                case "load": Load(restOfCommand);
                    break;
                case "mem": Mem(restOfCommand);
                    break;
                case "print": Print(restOfCommand);
                    break;
                default: ParseMathString(firstCommand, restOfCommand);
            }
        }

    }

    private static void ParseMathString(String firstCommand, String restOfCommand) {
        in.reset();

        String edits = restOfCommand.replaceAll("sqrt ", "&").trim();
        String newStr = edits.replaceAll("=", "").trim();
        String newStr2 = newStr.replaceAll(" ", "").trim();

        String strParsed = newStr2;

        //in = new Scanner(restOfCommand).useDelimiter("-|+|*|/|^|sqrt");
        String[] tempNums = strParsed.split("\\W");

        Double[] numbers = new Double[tempNums.length];
        for(int i = 0; i < numbers.length; i++){
            if (isNumeric(tempNums[i])){
                numbers[i] = Double.parseDouble(tempNums[i]);
            }else{
                if(lstVars.containsKey(tempNums[i])) {
                    numbers[i] = lstVars.get(tempNums[i]);
                    strParsed = strParsed.replaceAll(tempNums[i], (Double.toString(numbers[i])));

                }

            }
        }
        String moreStr = AddWhiteSpace(strParsed);
        String sqrtGone = moreStr.replaceAll(" 1 ", "1.0");
        String parsed = sqrtGone.replaceAll("& 1.0 ", "1.0");
        String finalParse = RemoveSqrt(parsed);
        eval doThis = new eval();
        System.out.print(doThis.performMath(finalParse));
        if(lstVars.containsKey(firstCommand)){
            lstVars.put(firstCommand, Calculate(finalParse));
        }

    }

    private static String AddWhiteSpace(String restOfCommand) {
        if(restOfCommand.contains("*")){
            restOfCommand = restOfCommand.replaceAll("\\*", " * ");
        }
        if(restOfCommand.contains("-")){
            restOfCommand = restOfCommand.replaceAll("-", " - ");
        }
        if(restOfCommand.contains("+")){
            restOfCommand = restOfCommand.replaceAll("\\+", " + ");
        }
        if(restOfCommand.contains("/")){
            restOfCommand = restOfCommand.replaceAll("/", " / ");
        }
        if(restOfCommand.contains("^")){
            restOfCommand = restOfCommand.replaceAll("\\^", " ^ ");
        }
        if(restOfCommand.contains("&")){
            restOfCommand = restOfCommand.replaceAll("\\&", "& ");
        }
        return restOfCommand.trim();
    }

    private static String RemoveSqrt(String strMath){
        String current = "";
        String returnStr = "";
        in.reset();
        in = new Scanner(strMath).useDelimiter(" ");
        String token = "";
        ArrayList<String> arr = new ArrayList<>();
        while (in.hasNext()) {
            token = in.next();
            if(token.equals("&")){
                current = in.next();
                returnStr += Double.toString(Math.sqrt(Double.parseDouble(current))) + " ";
            } else {
                returnStr += token + " ";
            }
        }
        in.reset();
        return returnStr;
    }

    private static Double Calculate(String strMath){
        String token;
        Double total = 0.;
        Double current = 0.;
        String operator = "";
        in = new Scanner(strMath).useDelimiter(" ");
        total = Double.parseDouble(in.next());
        while(in.hasNext()){
            token = in.next();
            if(isNumeric(token)){
                current = Double.parseDouble(token);
                switch(operator){
                    case "+": total += current;
                        break;
                    case "-": total = total - current;
                        break;
                    case "*": total = total * current;
                        break;
                    case "/": total = total / current;
                        break;
                    case "^": total = Math.pow(total, current);
                        break;
                }
            }else {
                operator = token;
            }

        }
        return total;
    }

    private static boolean isNumeric(String str){
        try
        {
            Double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }

    private static void Print(String restOfCommand) {
        if(lstVars.containsKey(restOfCommand)){
            System.out.println("VALUE OF " + restOfCommand + " IS " + lstVars.get(restOfCommand));
        }
    }

    private static void Mem(String restOfCommand) {
        lstVars.put(restOfCommand, 0.0);
    }

    private static void Load(String command) {
        in = new Scanner(System.in);
        String key = command;
        Double value = 0.0;
        System.out.println("$>ENTER VALUE FOR " + command.toUpperCase());
        System.out.print("$>");
        value = in.nextDouble();
        //tempVar.put(key, value);
        lstVars.put(key, value);
        //System.out.println(lstVars.get(key));
        in.nextLine();
        in.reset();
    }
}