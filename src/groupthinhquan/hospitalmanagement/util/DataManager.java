/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package groupthinhquan.hospitalmanagement.util;

import groupthinhquan.hospitalmanagement.core.*;
import groupthinhquan.hospitalmanagement.interaction.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardCopyOption.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UnknownFormatConversionException;
import java.util.stream.Stream;

/**
 *
 * @author Thinh
 */

public final class DataManager {
    public static final String DEFAULT_TEMPORARY_FILE_EXTENSION = ".bak";
    public static final String DEFAULT_DELIMITER = ";";
    
    // Used Data
    private EmployeeDatabase employeeDB;
    private PatientDatabase patientDB;
    private ScheduleSystem scheduleSystem;
    private AppointmentSystem appointmentSystem;
    
    // Backup Data
    private EmployeeDatabase employeeDB_BackUp;
    private PatientDatabase patientDB_BackUp;
    private ScheduleSystem scheduleSystem_BackUp;
    private AppointmentSystem appointmentSystem_BackUp;
    
    // Additional Fields
    private String delim;
    private String tempFileExtension;
    
    public DataManager() {
        initDBAndSystem();
        delim = DEFAULT_DELIMITER;
        tempFileExtension = DEFAULT_TEMPORARY_FILE_EXTENSION;
    }
    
    public DataManager(String delimiter) {
        initDBAndSystem();
        delim = delimiter;
        tempFileExtension = DEFAULT_TEMPORARY_FILE_EXTENSION;
    }
    
    public DataManager(String delimiter, String tempFileExtension) {
        initDBAndSystem();
        delim = delimiter;
        this.tempFileExtension = tempFileExtension;
    }
    
    private void initDBAndSystem() {
        employeeDB = new EmployeeDatabase();
        patientDB = new PatientDatabase();
        scheduleSystem = new ScheduleSystem();
        appointmentSystem = new AppointmentSystem();
        
        employeeDB_BackUp = new EmployeeDatabase();
        patientDB_BackUp = new PatientDatabase();
        scheduleSystem_BackUp = new ScheduleSystem();
        appointmentSystem_BackUp = new AppointmentSystem();
    }
    
    private static String fileNotFoundErrorMsg(String filePath) {
        return "File '" + filePath + "' not found!";
    }
    
    private static String fileNotCreatedErrorMsg(String filePath) {
        return "File '" + filePath + "' cannot be created!";
    }
    
    private static String fileFormatErrorMsg(String filePath) {
        return "Error while reading file '" + filePath + "'! Bad file format!";
    }
    
    private static String createTemporaryFileErrorMsg(String recordType) {
        return "An error occurred while trying to create temporary file when saving " + recordType + " records!";
    }
    
    private static String removeTemporaryFileErrorMsg(String recordType) {
        return "An error occurred while trying to remove temporary file when saving " + recordType + " records!";
    }
    
    private static String saveFileErrorMsg(String recordType) {
        return "An error occurred while trying to save " + recordType + " records!";
    }
    
    /* All loadXxxFromFile assume that files have comma as delimiter*/
    
    public void loadDoctorsFromFile(String filePath) throws IOException, 
        NumberFormatException, IllegalArgumentException {
        try(Stream<String> lines = Files.lines(Path.of(filePath))) {
            lines.map(line -> {
                String[] arr = line.split(delim, -1);
                int ID = Integer.parseInt(arr[0]);
                String name = arr[1];
                int age = Integer.parseInt(arr[2]);
                Sex sex = Sex.valueOf(arr[3]);
                String address = arr[4];
                String phoneNumber = arr[5];
                int workingYear = Integer.parseInt(arr[6]);
                double salary = Double.parseDouble(arr[7]);
                String[] majors = Arrays.copyOfRange(arr, 8, arr.length);

                return new Doctor(ID, name, age, sex, address, phoneNumber, workingYear, salary, majors);
           }).forEach(doctor -> employeeDB.add(doctor.getID(), doctor));
        }
        catch(IOException e) {
            throw new IOException(fileNotFoundErrorMsg(filePath));
        }
        catch(NumberFormatException e) {
            throw new NumberFormatException(fileFormatErrorMsg(filePath));
        }
        catch(IllegalArgumentException e) {
            throw new IllegalArgumentException(fileFormatErrorMsg(filePath));
        }
    }
    
    public void loadNursesFromFile(String filePath) throws IOException, 
        NumberFormatException, IllegalArgumentException {
        try(Stream<String> lines = Files.lines(Path.of(filePath))) {
            lines.map(line -> {
                String[] arr = line.split(delim, -1);
                int ID = Integer.parseInt(arr[0]);
                String name = arr[1];
                int age = Integer.parseInt(arr[2]);
                Sex sex = Sex.valueOf(arr[3]);
                String address = arr[4];
                String phoneNumber = arr[5];
                int workingYear = Integer.parseInt(arr[6]);
                double salary = Double.parseDouble(arr[7]);
                String position = arr[8];

                return new Nurse(ID, name, age, sex, address, phoneNumber, workingYear, salary, position);
           }).forEach(nurse -> employeeDB.add(nurse.getID(), nurse));
        }
        catch(IOException e) {
            throw new IOException(fileNotFoundErrorMsg(filePath));
        }
        catch(IllegalArgumentException e) {
            throw new IllegalArgumentException(fileFormatErrorMsg(filePath));
        }
    }
    
    public void loadPatientsFromFile(String filePath) throws IOException, 
        NumberFormatException, IllegalArgumentException {
        try(Stream<String> lines = Files.lines(Path.of(filePath))) {
            lines.map(line -> {
                String[] arr = line.split(delim, -1);
                int ID = Integer.parseInt(arr[0]);
                String name = arr[1];
                int age = Integer.parseInt(arr[2]);
                Sex sex = Sex.valueOf(arr[3]);
                String address = arr[4];
                String phoneNumber = arr[5];
                String detail = arr[6];

                return new Patient(ID, name, age, sex, address, phoneNumber, detail);
           }).forEach(patient -> patientDB.add(patient.getID(), patient));
        }
        catch(IOException e) {
            throw new IOException(fileNotFoundErrorMsg(filePath));
        }
        catch(IllegalArgumentException e) {
            throw new IllegalArgumentException(fileFormatErrorMsg(filePath));
        }
    }
    
    public void loadSchedulesFromFile(String filePath) 
        throws IOException, UnknownFormatConversionException {
        
        try(FileReader reader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(reader)) {
            
            String line = bufferedReader.readLine();
            if(line == null){
                return;
            }
            
            while(line != null) {
                ArrayList<String> lines = new ArrayList<>();
                lines.add(line);
                
                while(true) {
                    line = bufferedReader.readLine();
                    if(line != null && !line.matches(TextFieldFilteringSupporter.POSITIVE_INTEGER_REGEX)) {
                        lines.add(line);
                    }
                    else {
                        break;
                    }
                }
                
                int currentID = Integer.parseInt(lines.get(0));
                scheduleSystem.createSchedule(currentID);
                IndividualSchedule currentSchedule = scheduleSystem.accessSchedule(currentID);
                for(int i = 1; i < lines.size(); i++) {
                    String currentLine = lines.get(i);
                    String[] lineTokens = currentLine.split(delim);
                    Date currentDate = Date.parseDate(lineTokens[0]);
                    
                    for(int j = 1; j < lineTokens.length; j++) {
                        WorkingHour currentWH = WorkingHour.parseWorkingHour(lineTokens[j]);
                        currentSchedule.addEntry(currentDate, currentWH);
                    }
                }
            }
        }
        catch(IOException e) {
            throw new IOException(fileNotFoundErrorMsg(filePath));
        }
        catch(IllegalArgumentException e) {
            throw new IllegalArgumentException(fileFormatErrorMsg(filePath));
        }
    }
    
    public void loadAppointmentsFromFile(String filePath) throws IOException, 
        NumberFormatException, UnknownFormatConversionException {
        try(Stream<String> lines = Files.lines(Path.of(filePath))) {
            lines.forEach(line -> {
                String[] arr = line.split(delim, -1);
                String appointmentID = arr[0];
                Date date = Date.parseDate(arr[1]);
                Time time = Time.parseTime(arr[2]);
                int doctorID = Integer.parseInt(arr[3]);
                int patientID = Integer.parseInt(arr[4]);
                String detail = arr[5];
                
                appointmentSystem.addAppointment(new Appointment(appointmentID, date, time, detail), doctorID, patientID);
           });
        }
        catch(IOException e) {
            throw new IOException(fileNotFoundErrorMsg(filePath));
        }
        catch(NumberFormatException | NullPointerException e) {
            throw new NumberFormatException(fileFormatErrorMsg(filePath));
        }
    }
    
    public void saveDoctorsToFile(String filePath) throws IOException {
        try {
            saveDoctorsToFileImpl(filePath);
        }
        catch(IOException e) {
            Path targetFile = Paths.get(filePath);
            Path temporaryFile = Paths.get(filePath + tempFileExtension);
            if(Files.exists(temporaryFile)) {
                Files.move(temporaryFile, targetFile, REPLACE_EXISTING);
            }
            
            throw e;
        }
    }
    
    private void saveDoctorsToFileImpl(String filePath) throws IOException {
        try {
            File file = new File(filePath);
            File parent = file.getParentFile();
            if(parent != null) {
                parent.mkdirs();
            }
        }
        catch(Exception e) {
            throw new IOException(fileNotCreatedErrorMsg(filePath));
        }
        
        Path targetFile = Paths.get(filePath);
        Path temporaryFile = Paths.get(filePath + tempFileExtension);
        if(Files.exists(targetFile)) {
            try {
                Files.move(targetFile, temporaryFile, REPLACE_EXISTING);
            }
            catch(IOException e) {
                throw new IOException(createTemporaryFileErrorMsg("doctor"));
            }
        }
        
        try(FileWriter writer = new FileWriter(filePath);
            PrintWriter printer = new PrintWriter(writer)) {

            Arrays.asList(employeeDB.getDoctors()).forEach(doctor -> {
                printer.printf("%d,%s,%d,%s,%s,%s,%d,%f", 
                doctor.getID(), doctor.getName(), doctor.getAge(), doctor.getSex(), doctor.getAddress(), 
                doctor.getPhoneNum(), doctor.getWorkingYear(), doctor.getSalary());

                Arrays.asList(doctor.getMajors()).forEach(major -> printer.printf(",%s", major));
                printer.println();
            });
        }
        catch(IOException e) {
            throw new IOException(saveFileErrorMsg("doctor"));
        }
        
        try {
            Files.deleteIfExists(temporaryFile);
        }
        catch(IOException e) {
            throw new IOException(removeTemporaryFileErrorMsg("doctor"));
        }
    }
    
    public void saveNursesToFile(String filePath) throws IOException {
        try {
            saveNursesToFileImpl(filePath);
        }
        catch(IOException e) {
            Path targetFile = Paths.get(filePath);
            Path temporaryFile = Paths.get(filePath + tempFileExtension);
            if(Files.exists(temporaryFile)) {
                Files.move(temporaryFile, targetFile, REPLACE_EXISTING);
            }
            
            throw e;
        }
    }
    
    private void saveNursesToFileImpl(String filePath) throws IOException {
        try {
            File file = new File(filePath);
            File parent = file.getParentFile();
            if(parent != null) {
                parent.mkdirs();
            }
        }
        catch(Exception e) {
            throw new IOException(fileNotCreatedErrorMsg(filePath));
        }
        
        Path targetFile = Paths.get(filePath);
        Path temporaryFile = Paths.get(filePath + tempFileExtension);
        if(Files.exists(targetFile)) {
            try {
                Files.move(targetFile, temporaryFile, REPLACE_EXISTING);
            }
            catch(IOException e) {
                throw new IOException(createTemporaryFileErrorMsg("nurse"));
            }
        }
        
        try(FileWriter writer = new FileWriter(filePath);
            PrintWriter printer = new PrintWriter(writer)) {

            Arrays.asList(employeeDB.getNurses()).forEach(nurse -> {
                printer.printf("%d,%s,%d,%s,%s,%s,%d,%f,%s", 
                nurse.getID(), nurse.getName(), nurse.getAge(), nurse.getSex(), nurse.getAddress(), 
                nurse.getPhoneNum(), nurse.getWorkingYear(), nurse.getSalary(), nurse.getPosition());
                
                printer.println();
            });
        }
        catch(IOException e) {
            throw new IOException(saveFileErrorMsg("nurse"));
        }
        
        try {
            Files.deleteIfExists(temporaryFile);
        }
        catch(IOException e) {
            throw new IOException(removeTemporaryFileErrorMsg("nurse"));
        }
    }
    
    public void savePatientsToFile(String filePath) throws IOException {
        try {
            savePatientsToFileImpl(filePath);
        }
        catch(IOException e) {
            Path targetFile = Paths.get(filePath);
            Path temporaryFile = Paths.get(filePath + tempFileExtension);
            if(Files.exists(temporaryFile)) {
                Files.move(temporaryFile, targetFile, REPLACE_EXISTING);
            }
                
            throw e;
        }
    }
    
    private void savePatientsToFileImpl(String filePath) throws IOException {
        try {
            File file = new File(filePath);
            File parent = file.getParentFile();
            if(parent != null) {
                parent.mkdirs();
            }
        }
        catch(Exception e) {
            throw new IOException(fileNotCreatedErrorMsg(filePath));
        }
        
        Path targetFile = Paths.get(filePath);
        Path temporaryFile = Paths.get(filePath + tempFileExtension);
        if(Files.exists(targetFile)) {
            try {
                Files.move(targetFile, temporaryFile, REPLACE_EXISTING);
            }
            catch(IOException e) {
                throw new IOException(createTemporaryFileErrorMsg("patient"));
            }
        }
        
        try(FileWriter writer = new FileWriter(filePath);
            PrintWriter printer = new PrintWriter(writer)) {

            Arrays.asList(patientDB.getPatients()).forEach(patient -> {
                printer.printf("%d,%s,%d,%s,%s,%s,%s", 
                patient.getID(), patient.getName(), patient.getAge(), patient.getSex(), 
                patient.getAddress(), patient.getPhoneNum(), patient.getStatus());
                
                printer.println();
            });
        }
        catch(IOException e) {
            throw new IOException(saveFileErrorMsg("patient"));
        }
        
        try {
            Files.deleteIfExists(temporaryFile);
        }
        catch(IOException e) {
            throw new IOException(removeTemporaryFileErrorMsg("patient"));
        }
    }

    public void saveSchedulesToFile(String filePath) throws IOException {
        try {
            saveSchedulesToFileImpl(filePath);
        }
        catch(IOException e) {
            Path targetFile = Paths.get(filePath);
            Path temporaryFile = Paths.get(filePath + tempFileExtension);
            if(Files.exists(temporaryFile)) {
                Files.move(temporaryFile, targetFile, REPLACE_EXISTING);
            }
                
            throw e;
        }
    }
    
    private void saveSchedulesToFileImpl(String filePath) throws IOException {
        try {
            File file = new File(filePath);
            File parent = file.getParentFile();
            if(parent != null) {
                parent.mkdirs();
            }
        }
        catch(Exception e) {
            throw new IOException(fileNotCreatedErrorMsg(filePath));
        }
        
        Path targetFile = Paths.get(filePath);
        Path temporaryFile = Paths.get(filePath + tempFileExtension);
        if(Files.exists(targetFile)) {
            try {
                Files.move(targetFile, temporaryFile, REPLACE_EXISTING);
            }
            catch(IOException e) {
                throw new IOException(createTemporaryFileErrorMsg("schedule"));
            }
        }
        
        try(FileWriter writer = new FileWriter(filePath);
            BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
            for(int ID : scheduleSystem.getIDs()) {
                bufferedWriter.write(Integer.toString(ID) + "\n");
                
                var schedule = scheduleSystem.accessSchedule(ID);
                for(Date date : schedule.getDates()) {
                    bufferedWriter.write(date.toString());
                    
                    for(WorkingHour workingHour : schedule.getWorkingHours(date)) {
                        bufferedWriter.write(delim + 
                            workingHour.getStartTime() + "-" + workingHour.getEndTime());
                    }
                    
                    bufferedWriter.write("\n");
                }
            }
        }
        catch(IOException e) {
            throw new IOException(saveFileErrorMsg("schedule"));
        }
        
        try {
            Files.deleteIfExists(temporaryFile);
        }
        catch(IOException e) {
            throw new IOException(removeTemporaryFileErrorMsg("schedule"));
        }
    }
    
    public void saveAppointmentsToFile(String filePath) throws IOException {
        try {
            saveAppointmentsToFileImpl(filePath);
        }
        catch(IOException e) {
            Path targetFile = Paths.get(filePath);
            Path temporaryFile = Paths.get(filePath + tempFileExtension);
            if(Files.exists(temporaryFile)) {
                Files.move(temporaryFile, targetFile, REPLACE_EXISTING);
            }
                
            throw e;
        }
    }
    
    private void saveAppointmentsToFileImpl(String filePath) throws IOException {
        try {
            File file = new File(filePath);
            File parent = file.getParentFile();
            if(parent != null) {
                parent.mkdirs();
            }
        }
        catch(Exception e) {
            throw new IOException(fileNotCreatedErrorMsg(filePath));
        }
        
        Path targetFile = Paths.get(filePath);
        Path temporaryFile = Paths.get(filePath + tempFileExtension);
        if(Files.exists(targetFile)) {
            try {
                Files.move(targetFile, temporaryFile, REPLACE_EXISTING);
            }
            catch(IOException e) {
                throw new IOException(createTemporaryFileErrorMsg("appointment"));
            }
        }
        
        try(FileWriter writer = new FileWriter(filePath);
            PrintWriter printer = new PrintWriter(writer)) {

            Arrays.asList(appointmentSystem.getAppointments()).forEach(appointmentInfo -> {
                Appointment appointment = appointmentInfo.getAppointment();
                int doctorID = appointmentInfo.getDoctorID();
                int patientID = appointmentInfo.getPatientID();
                
                printer.printf("%s,%s,%s,%d,%d,%s", 
                appointment.getID(), appointment.getDate().toString(), appointment.getTime().toString(), 
                doctorID, patientID, appointment.getDetail());
                
                printer.println();
            });
        }
        catch(IOException e) {
            throw new IOException(saveFileErrorMsg("appointment"));
        }
        
        try {
            Files.deleteIfExists(temporaryFile);
        }
        catch(IOException e) {
            throw new IOException(removeTemporaryFileErrorMsg("appointment"));
        }
    }
    
    public void discardEmployeeChanges() throws CloneNotSupportedException {
        employeeDB = (EmployeeDatabase) employeeDB_BackUp.clone();
    }
    
    public void discardPatientChanges() throws CloneNotSupportedException {
        patientDB = (PatientDatabase) patientDB_BackUp.clone();
    }
    
    public void discardScheduleChanges() throws CloneNotSupportedException {
        scheduleSystem = (ScheduleSystem) scheduleSystem_BackUp.clone();
    }
    
    public void discardAppointmentChanges() throws CloneNotSupportedException {
        appointmentSystem = (AppointmentSystem) appointmentSystem_BackUp.clone();
    }
    
    public void discardAllChanges() throws CloneNotSupportedException {
        discardEmployeeChanges();
        discardPatientChanges();
        discardScheduleChanges();
        discardAppointmentChanges();
    }
    
    public void applyEmployeeChanges() throws CloneNotSupportedException {
        employeeDB_BackUp = (EmployeeDatabase) employeeDB.clone();
    }
    
    public void applyPatientChanges() throws CloneNotSupportedException {
        patientDB_BackUp = (PatientDatabase) patientDB.clone();
    }
    
    public void applyScheduleChanges() throws CloneNotSupportedException {
        scheduleSystem_BackUp = (ScheduleSystem) scheduleSystem.clone();
    }
    
    public void applyAppointmentChanges() throws CloneNotSupportedException {
        appointmentSystem_BackUp = (AppointmentSystem) appointmentSystem.clone();
    }
    
    public void applyAllChanges() throws CloneNotSupportedException {
        applyEmployeeChanges();
        applyPatientChanges();
        applyScheduleChanges();
        applyAppointmentChanges();
    }
    
    public void setDelimiter(String delimiter) {
        delim = delimiter;
    }
    
    public void setTempFileExtension(String extension) {
        tempFileExtension = extension;
    }
    
    public EmployeeDatabase getEmployeeDB() {
        return employeeDB;
    }

    public PatientDatabase getPatientDB() {
        return patientDB;
    }

    public ScheduleSystem getScheduleSystem() {
        return scheduleSystem;
    }

    public AppointmentSystem getAppointmentSystem() {
        return appointmentSystem;
    }
    
    public String getDelimiter() {
        return delim;
    }
    
    public String getTempFileExtension() {
        return tempFileExtension;
    }
}
