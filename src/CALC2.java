import java.util.*;

/**
 * Created by Giampiero Vanzzini on 10/14/2016.
 * Program: CALC 2
 */
public class CALC2 {

    //private static ArrayList<HashMap<String, Integer>> lstVars = new ArrayList<>();
    private static Map<String, Double> lstVars = new HashMap<>();
    private static Scanner in;
    public static void main(String[] args){
       // System.out.println("You must put a space between all \"sqrt\" and values or variables.");
        System.out.println("$>READY FOR INPUT");

        String command = "";
        while (true){ //this loops keeps running until you input "stop"
            System.out.print("$>");
            in = new Scanner(System.in); //takes command
            command = in.nextLine();
            if (command.contains("stop")){
                break;
            }
            ProcessCommand(command); //starts processing command
            in.reset();
        }
        in.close();
        System.exit(1);

    }

    private static void ProcessCommand(String command) {
        if(command.isEmpty()){ //dont waste time on empty commands
            //System.out.println();
            return;
        }
        String s = "";
        if(command.contains("//")) { //handles comments
            s = command.substring(0, command.indexOf("//"));
        }

        String temp;
        String newStr = command.replaceAll("=", " ").trim();
        String firstCommand = "";
        String restOfCommand = "";
        in = new Scanner(newStr).useDelimiter(" ");
        firstCommand = in.next().trim();
        if(firstCommand.length() > 0){
            temp = in.nextLine().trim();
            if(temp.contains("//")){
                String[] splitter = temp.split("//");
                restOfCommand = splitter[0];
            } else {
                restOfCommand = temp;
            }
            in.reset();
            switch(firstCommand){ //decides what to do with a command
                case "load": Load(restOfCommand);
                    break;
                case "mem": Mem(restOfCommand); //stores variable
                    break;
                case "print": Print(restOfCommand); //print variable
                    break;
                default: ParseMathString(firstCommand, restOfCommand); //evaluates math string
            }
        }

    }

    private static void ParseMathString(String firstCommand, String restOfCommand) {
        in.reset();

        //String edits = restOfCommand.replaceAll("sqrt ", "&").trim();

        String newStr2 = restOfCommand.replaceAll(" ", "").trim(); //remove white space

        String strParsed = newStr2; //it works so I'm leaving it here.

        //in = new Scanner(restOfCommand).useDelimiter("-|+|*|/|^|sqrt");
        String[] tempNums = strParsed.split("\\W"); //for getting variables in the string

        Double[] numbers = new Double[tempNums.length];
        for(int i = 0; i < numbers.length; i++){ //evaluate variables in the string before evaluating the string.
            if (isNumeric(tempNums[i])){
                numbers[i] = Double.parseDouble(tempNums[i]);
            }else{
                if(lstVars.containsKey(tempNums[i])) {
                    numbers[i] = lstVars.get(tempNums[i]);
                    strParsed = strParsed.replaceAll(tempNums[i], (Double.toString(numbers[i])));

                }

            }
        }
        String moreStr = AddWhiteSpace(strParsed); //put some white space back in there
        //deprecated:
//        System.out.println(moreStr);
//        System.out.println();
        //String sqrtGone = moreStr.replaceAll(" 1 ", "1.0");
        //String parsed = sqrtGone.replaceAll("& 1.0 ", "1.0");
        //String finalParse = RemoveSqrt(parsed);
        //eval doThis = new eval();
        //System.out.print(doThis.performMath(finalParse));
        if(lstVars.containsKey(firstCommand)){ //if the variable exists, do the math.
            lstVars.put(firstCommand, performMath(moreStr));
        }

    }

    private static String AddWhiteSpace(String restOfCommand) { //adds white space to a math string.
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
//        if(restOfCommand.contains("&")){
//            restOfCommand = restOfCommand.replaceAll("", "& ");
//        }
        return restOfCommand.trim();
    }

/*    deprecated:
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
    }*/

//this does math stuff.
    public static double performMath(final String str) { //this does all the math evaluation work
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            //        | number | functionName factor | factor `^` factor

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    x = parseFactor();
                    if (func.equals("sqrt")) x = Math.sqrt(x);
                    else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
                    else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
                    else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
                    else throw new RuntimeException("Unknown function: " + func);
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse();
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

    private static void Print(String restOfCommand) { //prints a variable
        if(lstVars.containsKey(restOfCommand)){
            System.out.println("VALUE OF " + restOfCommand + " IS " + lstVars.get(restOfCommand));
        }
    }

    private static void Mem(String restOfCommand) {
        lstVars.put(restOfCommand, 0.0);
    } //stores a variable

    private static void Load(String command) { //loads and stores a variable
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