package angularspringapp.dao;

import angularspringapp.entity.Audit;
import angularspringapp.entity.Payment;
import angularspringapp.entity.User;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Repository
@Transactional
public class MSSQLUserDAO {

    private static final Logger logger = Logger.getLogger(MSSQLUserDAO.class);

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return sessionFactory.getCurrentSession().createQuery("FROM User").list();
    }

    @Transactional(readOnly = true)
    public User findByName(String username) {
        Query query = sessionFactory.getCurrentSession().createQuery("FROM User u where u.username = :username");
        return (User) query.setParameter("username", username).uniqueResult();
    }

    @Transactional(readOnly = true)
    public List<User> findAllWithAudits() {
        return sessionFactory.getCurrentSession().createQuery("FROM User c left join fetch c.auditList").list();
    }

    @Transactional(readOnly = true)
    public List<User> findAllWithPayments() {
        return sessionFactory.getCurrentSession().createQuery("FROM User c left join fetch c.paymentList").list();
    }

    @Transactional(readOnly = true)
    public User findByNameWithPayments(String username) {
        Query query = sessionFactory.getCurrentSession().createQuery("FROM User c left join fetch c.paymentList where c.username = :username");
        return (User) query.setParameter("username", username).uniqueResult();
    }

    @Transactional(readOnly = true)
    public List<Audit> findByUser(String username) {
        Query query = sessionFactory.getCurrentSession().createQuery("FROM Audit a where a.user.username = :username");
        return query.setParameter("username", username).list();
    }

    @Transactional
    public void banUser(String username) {
        Query query = sessionFactory.getCurrentSession().createSQLQuery("UPDATE [user] SET banned='true' WHERE username=:username");
        query.setParameter("username", username).executeUpdate();
    }

    @Transactional
    public void unbanUser(String username) {
        Query query = sessionFactory.getCurrentSession().createSQLQuery("UPDATE [user] SET banned='false' WHERE username=:username");
        query.setParameter("username", username).executeUpdate();
    }

    public void saveAudit(String username, Boolean success) {
        SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery("EXEC generateAudit :username , :success");
        sqlQuery.setParameter("username", username).setParameter("success", success).executeUpdate();
    }

    @Transactional
    public void savePayments(Collection<Payment> payments) {
        Session currentSession = sessionFactory.getCurrentSession();
        for (Payment p : payments) {
            currentSession.saveOrUpdate(p);
        }
    }
}
