package ua.com.hse.notifyhseq;


public class NotifyHSEQItem {
    private int mainNumber;
    private int sync;
    private String dateRegistration;
    private String timeRegistration;
    private String dateHappened;
    private String timeHappened;
    private String type;
    private String place;
    private String department;
    private String description;
    private String photoPath;
    private String photoName;
    //status of notify (for exaple started, declined, in work etc.)
    private int status;
    //data of person who make notify
    private String namePerson;
    private String emailPerson;
    private String phonePerson;
    private String departmentPerson;

    public NotifyHSEQItem() {
    }

    public NotifyHSEQItem(int mainNumber, int sync, String dateRegistration, String timeRegistration,
                          String dateHappened, String timeHappened, String type, String place,
                          String department, String description, String photoPath, String photoName,
                          int status, String namePerson, String emailPerson, String phonePerson, String departmentPerson) {
        this.mainNumber = mainNumber;
        this.sync = sync;
        this.dateRegistration = dateRegistration;
        this.timeRegistration = timeRegistration;
        this.dateHappened = dateHappened;
        this.timeHappened = timeHappened;
        this.type = type;
        this.place = place;
        this.department = department;
        this.description = description;
        this.photoPath = photoPath;
        this.photoName = photoName;
        this.status = status;
        this.namePerson = namePerson;
        this.emailPerson = emailPerson;
        this.phonePerson = phonePerson;
        this.departmentPerson = departmentPerson;
    }

    public int getMainNumber() {
        return mainNumber;
    }

    public int getSync() {
        return sync;
    }

    public String getDateRegistration() {
        return dateRegistration;
    }

    public String getTimeRegistration() {
        return timeRegistration;
    }

    public String getDateHappened() {
        return dateHappened;
    }

    public String getTimeHappened() {
        return timeHappened;
    }

    public String getType() {
        return type;
    }

    public String getPlace() {
        return place;
    }

    public String getDepartment() {
        return department;
    }

    public String getDescription() {
        return description;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public String getPhotoName() {
        return photoName;
    }

    public int getStatus() {
        return status;
    }

    public String getNamePerson() {
        return namePerson;
    }

    public String getEmailPerson() {
        return emailPerson;
    }

    public String getPhonePerson() {
        return phonePerson;
    }

    public String getDepartmentPerson() {
        return departmentPerson;
    }
}
