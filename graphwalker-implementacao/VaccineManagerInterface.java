package org.isf.vaccine.test;// Generated by GraphWalker (http://www.graphwalker.org)
import org.graphwalker.java.annotation.Model;
import org.graphwalker.java.annotation.Vertex;
import org.graphwalker.java.annotation.Edge;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;

@Model(file = "updatedmodel.json")
public interface VaccineManagerInterface {

    @Edge()
    void e_deleteVaccine() throws OHException, OHServiceException;

    @Edge()
    void e_listVaccines() throws OHException, OHServiceException;

    @Edge()
    void e_createVaccine() throws OHException, OHServiceException;

    @Vertex()
    void v_VaccineCreated() throws OHException, OHServiceException;

    @Vertex()
    void v_VaccineDeleted() throws OHException, OHServiceException;

    @Edge()
    void e_updateVaccine() throws OHException, OHServiceException;

    @Vertex()
    void v_VaccineList() throws OHException, OHServiceException;

    @Edge()
void e_notFindVaccine() throws OHException, OHServiceException;

    @Edge()
    void e_findVaccine() throws OHException, OHServiceException;

    @Vertex()
    void v_VaccineUpdated() throws OHException, OHServiceException;
}
