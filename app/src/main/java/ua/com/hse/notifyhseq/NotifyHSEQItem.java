package ua.com.hse.notifyhseq;

/**
 * Created by UA809722 on 28.02.2017.
 */

public class NotifyHSEQItem {
    private int id;
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

    public NotifyHSEQItem() {
    }

    public NotifyHSEQItem(int id, String dateRegistration, String timeRegistration, String dateHappened, String timeHappened, String type, String place, String department, String description, String photoPath, String photoName) {
        this.id = id;
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
    }

    public int getId() {
        return id;
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
}
