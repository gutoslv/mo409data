/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2021 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
 *
 * Open Hospital is a free and open source software for healthcare data management.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.vaccine.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.InstanceOfAssertFactories.PATH;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.assertj.core.api.Condition;
import org.graphwalker.core.condition.EdgeCoverage;
import org.graphwalker.core.generator.CombinedPath;
import org.graphwalker.core.generator.RandomPath;
import org.graphwalker.core.generator.ShortestAllPaths;
import org.graphwalker.core.machine.Context;
import org.graphwalker.core.machine.ExecutionContext;
import org.graphwalker.core.machine.Machine;
import org.graphwalker.core.machine.SimpleMachine;
import org.graphwalker.io.factory.json.JsonModel;
import org.graphwalker.java.annotation.Edge;
import org.graphwalker.java.annotation.GraphWalker;
import org.graphwalker.java.annotation.Model;
import org.graphwalker.java.annotation.Vertex;
import org.graphwalker.java.test.TestBuilder;
import org.isf.OHCoreTestCase;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.vaccine.manager.VaccineBrowserManager;
import org.isf.vaccine.model.Vaccine;
import org.isf.vaccine.service.VaccineIoOperationRepository;
import org.isf.vaccine.service.VaccineIoOperations;
import org.isf.vactype.model.VaccineType;
import org.isf.vactype.service.VaccineTypeIoOperationRepository;
import org.isf.vactype.test.TestVaccineType;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Model(file = "updatedmodel.json")
@GraphWalker(value = "quick_random(vertex_coverage(100))", start = "v_VaccineList")
public class VaccinesTests extends OHCoreTestCase implements VaccineManagerInterface {

	Path modelPath = Paths.get("updatedmodel.json");
	private static TestVaccine testVaccine;
	private static TestVaccineType testVaccineType;

	@Autowired
	VaccineIoOperations vaccineIoOperation;
	@Autowired
	VaccineIoOperationRepository vaccineIoOperationRepository;
	@Autowired
	VaccineTypeIoOperationRepository vaccineTypeIoOperationRepository;
	@Autowired
	VaccineBrowserManager vaccineBrowserManager;

	@BeforeClass
	public static void setUpClass() {
		testVaccine = new TestVaccine();
		testVaccineType = new TestVaccineType();
	}

	@Before
	public void setUp() {
		cleanH2InMemoryDb();
	}


	public String _setupTestVaccine(boolean usingSet) throws OHException {
		VaccineType vaccineType = testVaccineType.setup(false);
		Vaccine vaccine = testVaccine.setup(vaccineType, usingSet);
		vaccineTypeIoOperationRepository.saveAndFlush(vaccineType);
		vaccineIoOperationRepository.saveAndFlush(vaccine);
		return vaccine.getCode();
	}

	public void _checkVaccineIntoDb(String code) throws OHServiceException {
		Vaccine foundVaccine = vaccineIoOperation.findVaccine(code);
		testVaccine.check(foundVaccine);
	}

	@Edge()
	@Override
	public void e_deleteVaccine() throws OHServiceException, OHException {
		VaccineType vaccineType = testVaccineType.setup(false);
		vaccineTypeIoOperationRepository.saveAndFlush(vaccineType);
		Vaccine vaccine = testVaccine.setup(vaccineType, true);
		vaccineIoOperationRepository.saveAndFlush(vaccine);
		vaccineBrowserManager.deleteVaccine(vaccine);
	}
	@Edge()
	@Override
	public void e_listVaccines() throws OHException, OHServiceException {
		_setupTestVaccine(false);
	}
	@Edge()
	@Override
	public void e_createVaccine() throws OHException, OHServiceException {
		VaccineType vaccineType = testVaccineType.setup(true);
		vaccineTypeIoOperationRepository.saveAndFlush(vaccineType);
		Vaccine vaccine = testVaccine.setup(vaccineType, true);
		vaccineBrowserManager.newVaccine(vaccine);
	}

	@Vertex()
	@Override
	public void v_VaccineCreated() throws OHServiceException {
		assertThat(vaccineBrowserManager.getVaccine()).isNotEmpty();
	}

	@Vertex()
	@Override
	public void v_VaccineDeleted() throws OHServiceException {
		assertThat(vaccineBrowserManager.getVaccine()).isEmpty();
	}

	@Edge()
	@Override
	public void e_updateVaccine() throws OHException {
		String code = _setupTestVaccine(false);
		Vaccine foundVaccine = vaccineBrowserManager.findVaccine("Z");
		foundVaccine.setDescription("TestDescription");
	}

	@Vertex()
	@Override
	public void v_VaccineList() throws OHServiceException {
		//assert that the returned list exists
		assertThat(vaccineBrowserManager.getVaccine()).isInstanceOf(List.class);
	}

	@Edge()
	@Override
	public void e_notFindVaccine() throws OHServiceException {
		vaccineBrowserManager.getVaccine();
	}
	@Edge()
	@Override
	public void e_findVaccine() throws OHException, OHServiceException {
		vaccineBrowserManager.getVaccine();
	}

	@Vertex()
	@Override
	public void v_VaccineUpdated() {
		assertThat(vaccineBrowserManager.findVaccine("Z").getDescription()).isEqualTo("TestDescription");
	}

	@Test
	public void runVaccinesTests() throws OHException, OHServiceException {
		// Execute the test steps
		v_VaccineList();
		e_deleteVaccine();
		v_VaccineDeleted();
		e_notFindVaccine();
		v_VaccineList();
	}

	@Test
	public void runVaccinesTests2() throws OHException, OHServiceException {
		// Execute the test steps
		v_VaccineList();
		e_updateVaccine();
		v_VaccineUpdated();
		e_findVaccine();
		v_VaccineList();
	}

	@Test
	public void runVaccinesTests3() throws OHException, OHServiceException {
		// Execute the test steps
		v_VaccineList();
		e_createVaccine();
		v_VaccineCreated();
		e_findVaccine();
		v_VaccineList();
	}

	@Test
	public void runVaccinesTests4() throws OHException, OHServiceException {
		// Execute the test steps
		v_VaccineList();
		e_listVaccines();
		v_VaccineList();
	}
}
