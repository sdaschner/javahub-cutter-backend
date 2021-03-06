package net.java.javahub.backend.prints.boundary;


import net.java.javahub.backend.images.control.Images;
import net.java.javahub.backend.prints.control.Votes;
import net.java.javahub.backend.prints.entity.Print;
import net.java.javahub.backend.prints.entity.Vote;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.InputStream;
import java.util.List;
import java.util.NoSuchElementException;

@Stateless
public class Prints {

    @Inject
    Images images;

    @Inject
    Votes votes;

    @PersistenceContext
    EntityManager entityManager;

    public Print create(final String name) {
        final Print print = new Print(name);
        return entityManager.merge(print);
    }

    public void addImage(final String id, final InputStream imageInput) {
        final Print print = retrievePrint(id);
        images.addImage(print, imageInput);
        entityManager.merge(print);
    }

    public Print getPrint(final String id) {
        return entityManager.find(Print.class, id);
    }

    public byte[] getImageData(final String id) {
        final Print print = retrievePrint(id);
        return print.getImageData();
    }

    public List<Print> getPrints() {
        return entityManager.createNamedQuery(Print.QUERY_FIND_ALL, Print.class).getResultList();
    }

    public Print getPrintInProgress() {
        // TODO implement
        final List<Print> prints = entityManager.createNamedQuery(Print.QUERY_FIND_ALL, Print.class).getResultList();
        if (prints.isEmpty())
            return null;
        return prints.get(0);
    }

    public List<Vote> getVotes() {
        return votes.getVotes();
    }

    public void resetVotes() {
        votes.resetVotes();
    }

    public void delete(final String id) {
        final Print print = retrievePrint(id);
        entityManager.remove(print);
        entityManager.flush();
    }

    private Print retrievePrint(final String id) {
        final Print print = entityManager.find(Print.class, id);
        if (print == null)
            throw new NoSuchElementException("Could not find print with id " + id);
        return print;
    }

}
