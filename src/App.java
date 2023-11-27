import java.sql.*;
import java.util.Scanner;

//to get current date
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class App {
    public static void main(String[] args) throws Exception {
        Scanner scn = new Scanner(System.in);

        final String dbUrl = "jdbc:mysql://localhost:3306/atm_interface?serverTimezone=UTC";
        final String user = "root";
        final String pwdd = "sanju1304";

        System.out.print("\nWelcome To Sanjana's ATM\nNEW USER OR OLD USER? ");
        String usertype = scn.next();

        if (usertype.toLowerCase().equals("new")) {
            System.out.println("Hey newbieee :) \n\nCREATE AN ACCOUNT FOR HASSLE-FREE TRANSACTIONS: ");

            System.out.print("NAME: ");
            String name = scn.next();

            System.out.print("CREATE PIN: ");
            String pin = scn.next();

            System.out.print("DEPOSIT AN INITIAL AMOUNT: ");
            String amt = scn.next();

            Connection conn1 = null;
            Statement stmt = null;

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conn1 = DriverManager.getConnection(dbUrl,user,pwdd);
                stmt = conn1.createStatement();

                String query1 = "select max(sno) from atm";
                ResultSet snoDeets = stmt.executeQuery(query1);

                String userid = "";
                while (snoDeets.next()) {
                    int sno = Integer.parseInt(snoDeets.getString("max(sno)"))+1;
                    userid = name.substring(0,3)+sno;
                }
                String query2 = "insert into atm(userid,name,pin,balance) values('"+userid+"','"+name+"',"+pin+","+amt+")";
                stmt.executeUpdate(query2);

                System.out.println("ATM ACCOUNT CREATED SUCCESSFULLY!\n YOU CAN USE "+userid+" AS USER ID FOR SIGNING IN");

            } catch (Exception e) {
                System.out.println(e);
            }
        }
        
        else if (usertype.toLowerCase().equals("old")) {
            System.out.println("WELCOME BACK ;)\n");

            System.out.println("USER ID: ");
            String Luserid = scn.next();

            System.out.println("PIN: ");
            String Lpin = scn.next();

            Connection conn2 = null;
            Statement stmt = null;

            try {
                //Class.forName("com.mysql.cj.jdbc.Driver");
                conn2 = DriverManager.getConnection(dbUrl,user,pwdd);
                stmt = conn2.createStatement();

                String query3 = "select name,userid,pin from atm where userid='"+Luserid+"'";
                ResultSet loginDeets = stmt.executeQuery(query3);

                while (loginDeets.next()) {
                    String userVal = loginDeets.getString("userid");
                    String pinVal = loginDeets.getString("pin");
                    String nameVal = loginDeets.getString("name");

                    if (Luserid.equals(userVal) && Lpin.equals(pinVal)) {
                        System.out.println("LOGIN SUCCESSFULL :)\nWELCOME BACK "+nameVal);

                        System.out.println("WHAT DO YOU WANT TO DO?");
                        System.out.println("1. WITHDRAW AMOUNT\n2. DEPOSIT AMOUNT\n3. TRANSFER AMOUNT\n4. VIEW TRANSACTION HISTORY\n5. CHECK BANK BALANCE\n6.RESET PIN\n");

                        System.out.print("ENTER YOUR CHOICE: ");
                        int op_choice = scn.nextInt();

                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                        LocalDateTime now = LocalDateTime.now();
                        String currDate = dtf.format(now);
                        //System.out.println(dtf.format(now));

                        
                        if (op_choice==1) {
                            conn2 = DriverManager.getConnection(dbUrl,user,pwdd);
                            stmt = conn2.createStatement();

                            System.out.print("ENTER THE AMOUNT YOU WANT TO WITHDRAW: ");
                            Double wthdrwAmt = scn.nextDouble();

                            String query4 = "select balance from atm where userid='"+userVal+"'";
                            ResultSet balDeets = stmt.executeQuery(query4);

                            String currBal="";
                            while (balDeets.next()) {
                                currBal = balDeets.getString("balance");
                            }

                            Double currBalDouble = Double.parseDouble(currBal);
                            if (wthdrwAmt>currBalDouble) {
                                System.out.println("INSUFFICIENT BALANCE !");
                            }
                            else if(wthdrwAmt>0){
                                String newBal = String.valueOf(currBalDouble - wthdrwAmt);

                                String wthQuery = "update atm set balance="+newBal+"where userid='"+userVal+"'";
                                stmt.executeUpdate(wthQuery);

                                String transQuery = "insert into trans_history(userid,name,type,old_balance,new_balance,amount,dateof_transaction) values('"+userVal+"','"+nameVal+"','WITHDRAWAL    ',"+currBal+","+newBal+","+wthdrwAmt+",'"+currDate+"')";
                                stmt.executeUpdate(transQuery);

                                System.out.println("YOUR AMOUNT OF RS. "+wthdrwAmt+" WITHDRAWN SUCCESSFULLY!\nYOUR NEW BALANCE IS "+newBal);
                            }
                            else{
                                System.out.println("ENTER A VALID AMOUNT");
                            }


                        }
                        
                        else if (op_choice==2) {
                            conn2 = DriverManager.getConnection(dbUrl,user,pwdd);
                            stmt = conn2.createStatement();

                            System.out.print("ENTER THE AMOUNT YOU WANT TO DEPOSIT: ");
                            Double depAmt = scn.nextDouble();

                            String query4 = "select balance from atm where userid='"+userVal+"'";
                            ResultSet balDeets = stmt.executeQuery(query4);

                            String currBal="";
                            while (balDeets.next()) {
                                currBal = balDeets.getString("balance");
                            }
                            Double currBalDouble = Double.parseDouble(currBal);
                            if (depAmt>0) {
                                String newBal = String.valueOf(currBalDouble + depAmt);

                                String wthQuery = "update atm set balance="+newBal+"where userid='"+userVal+"'";
                                stmt.executeUpdate(wthQuery);

                                String transQuery = "insert into trans_history(userid,name,type,old_balance,new_balance,amount,dateof_transaction) values('"+userVal+"','"+nameVal+"','DEPOSITION    ',"+currBal+","+newBal+","+depAmt+",'"+currDate+"')";
                                stmt.executeUpdate(transQuery);

                                System.out.println("YOUR AMOUNT OF RS. "+depAmt+" DEPOSITED SUCCESSFULLY!\nYOUR NEW BALANCE IS "+newBal);
                            
                            }
                            else{
                                System.out.println("ENTER A VALID AMOUNT TO DEPOSIT");
                            }



                        }
                        
                        else if (op_choice==3) {
                            conn2 = DriverManager.getConnection(dbUrl,user,pwdd);
                            stmt = conn2.createStatement();

                            //sender balance
                            String query4 = "select balance from atm where userid='"+userVal+"'";
                            ResultSet balDeets = stmt.executeQuery(query4);

                            String currBal="";
                            while (balDeets.next()) {
                                currBal = balDeets.getString("balance");
                            }
                            Double currBalDouble = Double.parseDouble(currBal);

                            System.out.println("ENTER THE RECIPIENT'S USER ID: ");
                            String Ruserid = scn.next();


                            System.out.println("ENTER THE AMOUNT YOU WANT TO TRANSFER: ");
                            Double tAmt = scn.nextDouble();



                            if (tAmt<=0) {
                                System.out.println("ENTER A VALID AMOUNT");
                            }
                            else if (tAmt>currBalDouble) {
                                System.out.println("INSUFFICIENT BALANCE");
                            }
                            else{
                                //transfer here
                                String newBal = String.valueOf((currBalDouble - tAmt));

                                //sender side
                                String query6 = "update atm set balance="+newBal+" where userid='"+userVal+"'";
                                stmt.executeUpdate(query6);

                                String query5 = "insert into trans_history(userid,name,type,transfer_to,old_balance,new_balance,amount,dateof_transaction) values('"+userVal+"','"+nameVal+"','AMOUNT TRANSFER','"+Ruserid+"','"+currBal+"','"+newBal+"','"+tAmt+"','"+currDate+"')";
                                stmt.executeUpdate(query5);


                                //receiver side
                                String query9 = "select name,balance from atm where userid='"+Ruserid+"'";
                                ResultSet RbalDeets = stmt.executeQuery(query9);

                                String RcurrBal="";
                                String Rname="";
                                while (RbalDeets.next()) {
                                    RcurrBal = RbalDeets.getString("balance");
                                    Rname = RbalDeets.getString("name");
                                }
                                Double RcurrBalDouble = Double.parseDouble(RcurrBal);

                                String RnewBal = String.valueOf(RcurrBalDouble+tAmt);

                                String query7 = "update atm set balance="+RnewBal+" where userid='"+Ruserid+"'";
                                stmt.executeUpdate(query7);

                                String query8 = "insert into trans_history(userid,name,type,transfer_to,old_balance,new_balance,amount,dateof_transaction) values('"+Ruserid+"','"+Rname+"','AMOUNT RECEIVED','"+userVal+"','"+RcurrBal+"','"+RnewBal+"','"+tAmt+"','"+currDate+"')";
                                stmt.executeUpdate(query8);

                                System.out.println("AMOUNT TRANSFER SUCCESSFUL!\nYOUR NEW BALANCE IS : "+newBal);

                            }
                        }
                        
                        else if (op_choice==4) {
                            //view history
                            conn2 = DriverManager.getConnection(dbUrl,user,pwdd);
                            stmt = conn2.createStatement();

                            String query4 = "select dateof_transaction,type,transfer_to,old_balance,amount,new_balance from trans_history where userid='"+userVal+"'";
                            ResultSet transDeets = stmt.executeQuery(query4);

                            //String result = "";
                            int count=1;
                            System.out.println("\nBELOW IS YOUR TRANSACTION HISTORY\n\n");
                            System.out.println("TRANSACTION NO. | DATE      | TYPE           | TRANSFER TO/FROM | OLD BALANCE | AMOUNT  | NEW BALANCE");
                            while (transDeets.next()) {
                                String date = transDeets.getString("dateof_transaction");
                                String type = transDeets.getString("type");
                                String transferTo = transDeets.getString("transfer_to");
                                String oldBal = transDeets.getString("old_balance");
                                String amnt = transDeets.getString("amount");
                                String newBal = transDeets.getString("new_balance");

                                System.out.println(count+"               |"+date+" |"+type+" |"+transferTo+"              |"+oldBal+"        |"+amnt+"   |"+newBal);

                                count++;
                            }

                        }

                        else if (op_choice==5) {
                            conn2 = DriverManager.getConnection(dbUrl,user,pwdd);
                            stmt = conn2.createStatement();

                            String query4 = "select balance from atm where userid='"+userVal+"'";
                            ResultSet balDeets = stmt.executeQuery(query4);

                            String currBal="";
                            while (balDeets.next()) {
                                currBal = balDeets.getString("balance");
                            }
                            System.out.println("YOUR BALANCE IS: "+currBal);
                        }
                        
                        else if (op_choice==6) {
                            System.out.println("ENTER THE NEW PIN: ");
                            String newPin = scn.next();

                            conn2 = DriverManager.getConnection(dbUrl,user,pwdd);
                            stmt = conn2.createStatement();

                            String query4 = "update atm set pin="+newPin+" where userid='"+userVal+"'";
                            stmt.executeUpdate(query4);
                            
                            System.out.println("PIN RESET SUCCESSFUL !");

                        }
                        
                        else{
                            System.out.println("ENTER OPTIONS FROM 1-6");
                        }
                    }
                    
                    else{
                        System.out.println("\nOOPS :) \nLOGIN FAILED!\nINCORRECT LOGIN CREDENTIALS");
                    }

                }
                
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        else{
            System.out.println("ENTER EITHER 'NEW' OR OLD' !");
        }
        scn.close();

    }
}