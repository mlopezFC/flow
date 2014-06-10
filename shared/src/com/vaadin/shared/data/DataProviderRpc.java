/*
 * Copyright 2000-2013 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.shared.data;

import com.vaadin.shared.communication.ClientRpc;

/**
 * RPC interface used for pushing container data to the client.
 * 
 * @since 7.2
 * @author Vaadin Ltd
 */
public interface DataProviderRpc extends ClientRpc {

    /**
     * Sends updated row data to a client.
     * <p>
     * rowDataJson represents a JSON array of JSON objects in the following
     * format:
     * 
     * <pre>
     * [{
     *   "d": [COL_1_JSON, COL_2_json, ...],
     *   "k": "1"
     * },
     * ...
     * ]
     * </pre>
     * 
     * where COL_INDEX is the index of the column (as a string), and COL_n_JSON
     * is valid JSON of the column's data.
     * 
     * @param firstRowIndex
     *            the index of the first updated row
     * @param rowDataJson
     *            the updated row data
     * @see com.vaadin.shared.ui.grid.GridState#JSONKEY_DATA
     * @see com.vaadin.ui.components.grid.Renderer#encode(Object)
     */
    public void setRowData(int firstRowIndex, String rowDataJson);

    /**
     * Informs the client to remove row data.
     * 
     * @param firstRowIndex
     *            the index of the first removed row
     * @param count
     *            the number of rows removed from <code>firstRowIndex</code> and
     *            onwards
     */
    public void removeRowData(int firstRowIndex, int count);

    /**
     * Informs the client to insert new row data.
     * 
     * @param firstRowIndex
     *            the index of the first new row
     * @param count
     *            the number of rows inserted at <code>firstRowIndex</code>
     */
    public void insertRowData(int firstRowIndex, int count);
}
