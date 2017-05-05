package ua.com.hse.notifyhseq;


import java.io.Serializable;

public class NotifyHSEQItem implements Serializable {
    private int uid;
    private Long timeRegistration;
    private Long timeHappened;
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

    public NotifyHSEQItem(int uid, Long timeRegistration,
                          Long timeHappened, String type, String place,
                          String department, String description, String photoPath, String photoName,
                          int status, String namePerson, String emailPerson, String phonePerson, String departmentPerson) {
        this.uid = uid;
        this.timeRegistration = timeRegistration;
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

    public int getUid() {
        return uid;
    }

    public Long getTimeRegistration() {
        return timeRegistration;
    }

    public Long getTimeHappened() {
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
