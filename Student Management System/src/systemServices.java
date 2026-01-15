import java.io.*;
import java.util.Scanner;

public class systemServices {
    //LOADS STUDENTS FROM THE FILE INTO THE ARRAY
    public static int loadStudents(server.Student[] arr) {
        int n = 0;

        try (Scanner fin = new Scanner(new FileInputStream("Students.txt"))) {

            while (fin.hasNextLine()) {
                String str = fin.nextLine().trim();

                // 🔥 Skip empty lines
                if (str.isEmpty()) {
                    continue;
                }

                // 🔥 Check that the line has at least 3 semicolons
                int first = str.indexOf(";");
                int second = str.indexOf(";", first + 1);
                int third = str.indexOf(";", second + 1);

                if (first == -1 || second == -1 || third == -1) {
                    // malformed line → skip safely
                    continue;
                }

                // Now it's safe to parse
                arr[n] = new server.Student();
                arr[n].id = str.substring(0, first);
                arr[n].fullName = str.substring(first + 1, second);
                arr[n].yearOfStudy = Integer.parseInt(str.substring(second + 1, third));
                arr[n].avgGrade = Double.parseDouble(str.substring(third + 1));

                n++;
            }

        } catch (FileNotFoundException e) {
            System.out.println("Error: Students.txt not found");
            return -1;
        }

        return n;
    }

    //PRINTS THE STATISTICS OF ALL THE STUDENTS
    public static String overallStatistics(int n, server.Student[] arr){
        double avg=0,highest=Double.MIN_VALUE,lowest=Double.MAX_VALUE;
        int ihighest=-1,ilowest=-1,passed=0,failed=0;
        for(int i=0; i<n; i++){
            avg+=arr[i].avgGrade;
            if(arr[i].avgGrade>highest){
                highest=arr[i].avgGrade;
                ihighest=i;
            }
            if(arr[i].avgGrade<lowest){
                lowest=arr[i].avgGrade;
                ilowest=i;
            }
            if(arr[i].avgGrade>=50) passed++;
            else failed++;
        }
        avg/=n;
        return String.format("""
            === OVERALL STATISTICS ===

            Total students: %d
            Average grade: %.2f
            Highest grade: %.2f (%s - %s)
            Lowest grade: %.2f (%s - %s)
            Passed: %.2f%%
            Failed: %.2f%%
            """,
                n, avg,
                highest, arr[ihighest].id, arr[ihighest].fullName,
                lowest,  arr[ilowest].id,  arr[ilowest].fullName,
                (double) passed / n * 100,
                (double) failed / n * 100
        );
    }

    //PRINTS THE STUDENTS THAT STUDY IN THAT SPECIFIC YEAR AS A STRING
    public static String getStudentsByYearAsString(int n, server.Student[] arr, String yearStr) {
        StringBuilder sb = new StringBuilder();
        boolean found = false;
        int year;
        try {
            year = Integer.parseInt(yearStr);

            if (year < 1 || year > 4) {
                sb.append("Year is out of range\n").append("\n");
                return sb.toString();
            }
        } catch (NumberFormatException e) {
            // 2. Response block: invalid number
            sb.append("Year must be a number\n").append("\n");
            return sb.toString();
        }
        sb.append("=== STUDENTS IN YEAR ").append(year).append(" ===\n");

        for (int i = 0; i < n; i++) {
            if (arr[i].yearOfStudy == year) {
                found = true;
                sb.append(arr[i].id).append(" - ")
                        .append(arr[i].fullName).append(" - ")
                        .append(arr[i].yearOfStudy).append(" - ")
                        .append(String.format("%.2f", arr[i].avgGrade))
                        .append("\n");
            }
        }

        if (!found) {
            sb.append("No students found in that year.\n");
        }

        sb.append("\n");
        return sb.toString();
    }

    // RETURNS ALL STUDENTS WHOSE NAME CONTAINS THE SUBSTRING AS A STRING
    public static String getStudentsByNameAsString(int n, server.Student[] arr, String substring) {
        StringBuilder sb = new StringBuilder();
        boolean found = false;

        sb.append("=== STUDENTS MATCHING \"")
                .append(substring)
                .append("\" ===\n");

        for (int i = 0; i < n; i++) {
            if (arr[i].fullName.toLowerCase().contains(substring.toLowerCase())) {
                found = true;
                sb.append(arr[i].id).append(" - ")
                        .append(arr[i].fullName).append(" - ")
                        .append(arr[i].yearOfStudy).append(" - ")
                        .append(String.format("%.2f", arr[i].avgGrade))
                        .append("\n");
            }
        }

        if (!found) {
            sb.append("No students matched your search.\n");
        }

        sb.append("\n");
        return sb.toString();
    }

    //SORTS THE STUDENTS
    public static String getStudentsSortedAsString(int n, server.Student[] arr, String orderStr){
        int order;
        StringBuilder sb = new StringBuilder();
        try {
            order = Integer.parseInt(orderStr);

            if (order < 1 || order > 3) {
                sb.append("The option has not been implemented yet\n").append("\n");
                return sb.toString();
            }
        } catch (NumberFormatException e) {
            // 2. Response block: invalid number
            sb.append("Order must be a number\n").append("\n");
            return sb.toString();
        }
        for(int i=0; i<n; i++){
            for(int j=i+1; j<n; j++){
                if(order==1&&arr[i].avgGrade<arr[j].avgGrade){
                    server.Student tmp=arr[i];
                    arr[i]=arr[j];
                    arr[j]=tmp;
                }
                else if(order==2&&arr[i].avgGrade>arr[j].avgGrade){
                    server.Student tmp=arr[i];
                    arr[i]=arr[j];
                    arr[j]=tmp;
                }
                else if(order==3&&arr[i].fullName.compareToIgnoreCase(arr[j].fullName)>0){
                    server.Student tmp=arr[i];
                    arr[i]=arr[j];
                    arr[j]=tmp;
                }
            }
        }
        sb.append(String.format("%4s%7s%20s%10s\n", "Rank", "ID", "Name", "Grade"));
        for (int i = 0; i < n; i++) {
            sb.append(String.format("%1d%10s%20s%10.2f\n",
                    i + 1,
                    arr[i].id,
                    arr[i].fullName,
                    arr[i].avgGrade));
        }
        sb.append("\n");
        return sb.toString();
    }

    //EXPORTS THE FAILING STUDENTS INTO A FILE
    public static void exportFailingStudents(int n, server.Student[] arr,Boolean type,PrintWriter out) {
        try (PrintWriter fout = new PrintWriter("failing_students.txt")) {
            int cnt = 0;
            for (int i = 0; i < n; i++) {
                if (arr[i].avgGrade < 50) {
                    fout.println(arr[i].id + ";" + arr[i].fullName + ";" +arr[i].yearOfStudy + ";" + arr[i].avgGrade);
                    cnt++;
                }
            }
            if(type) System.out.println("Exported " + cnt + " failing students.");
            else {
                out.println("Exported " + cnt + " failing students.");
                out.println("");
            }
        } catch (FileNotFoundException e) {
            if(type) System.out.println("Error: Cannot create output file.");
            else{
                out.println("Error: Cannot create output file.");
                out.println("");
            }
        }
    }

    //WRITES THE NEW STUDENT INTO THE FILE
    public static void addStudentToFile(server.Student s,Boolean type, PrintWriter out) {
        try (FileWriter fw = new FileWriter("Students.txt", true);
            PrintWriter tofile = new PrintWriter(fw)) {
            tofile.println(s.id + ";" + s.fullName + ";" + s.yearOfStudy + ";" + s.avgGrade);
            if(type)System.out.println("Student added successfully.");
            else{
                out.println("Student added successfully");
                out.println("");
            }

        } catch (Exception e) {
            if(type) System.out.println("Error writing to file.");
            else{
                out.println("Error writing to file.");
                out.println("");
            }
        }
    }
}
