/*
 * Copyright (c) 2017 Memorial Sloan-Kettering Cancer Center.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF MERCHANTABILITY OR FITNESS
 * FOR A PARTICULAR PURPOSE. The software and documentation provided hereunder
 * is on an "as is" basis, and Memorial Sloan-Kettering Cancer Center has no
 * obligations to provide maintenance, support, updates, enhancements or
 * modifications. In no event shall Memorial Sloan-Kettering Cancer Center be
 * liable to any party for direct, indirect, special, incidental or
 * consequential damages, including lost profits, arising out of the use of this
 * software and its documentation, even if Memorial Sloan-Kettering Cancer
 * Center has been advised of the possibility of such damage.
 */

/*
 * This file is part of cBioPortal CMO-Pipelines.
 *
 * cBioPortal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.cbioportal.database.annotator.model;

/**
 *
 * @author heinsz
 */

public class Mutation {
    private Integer MUTATION_EVENT_ID;
    private Integer SAMPLE_ID;

    public Mutation() {}

    public Mutation(Integer MUTATION_EVENT_ID, Integer SAMPLE_ID) {
        this.MUTATION_EVENT_ID = MUTATION_EVENT_ID;
        this.SAMPLE_ID = SAMPLE_ID;
    }

    public Integer getMUTATION_EVENT_ID() {
        return MUTATION_EVENT_ID;
    }

    public void setMUTATION_EVENT_ID(Integer MUTATION_EVENT_ID) {
        this.MUTATION_EVENT_ID = MUTATION_EVENT_ID;
    }

    public Integer getSAMPLE_ID() {
        return SAMPLE_ID;
    }

    public void setSAMPLE_ID(Integer SAMPLE_ID) {
        this.SAMPLE_ID = SAMPLE_ID;
    }
}
