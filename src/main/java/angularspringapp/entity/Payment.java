package angularspringapp.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "[payment]")
public class Payment implements Serializable {

    private Integer month;
    private Integer year;
    private String serviceName;
    private int amount;
    private User user;

    @ManyToOne
    @JoinColumn(name = "user_login")
    public User getUser() {
        return user;
    }

    @Column(name = "service_name")
    public String getServiceName() {
        return serviceName;
    }

    @Column(name = "amount")
    public int getAmount() {
        return amount;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Id
    @Column(name = "month")
    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    @Id
    @Column(name = "year")
    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Payment payment = (Payment) o;

        if (!month.equals(payment.month)) return false;
        if (!year.equals(payment.year)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = month.hashCode();
        result = 31 * result + year.hashCode();
        return result;
    }
}
