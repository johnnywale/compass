package org.compass.core.test.querybuilder.polyaliasquerystring;

import org.compass.annotations.Searchable;
import org.compass.annotations.SearchableId;
import org.compass.annotations.SearchableProperty;

import com.gigaspaces.license.LicenseGenerator;

/**
 * @author kimchy
 */
@Searchable
public class C {

	@SearchableId
	int id;

	@SearchableProperty
	String cvalue;

}
