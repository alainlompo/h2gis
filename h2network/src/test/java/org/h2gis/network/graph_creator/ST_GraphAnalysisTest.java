/**
 * h2spatial is a library that brings spatial support to the H2 Java database.
 *
 * h2spatial is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
 *
 * h2patial is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * h2spatial is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * h2spatial. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly: info_at_orbisgis.org
 */

package org.h2gis.network.graph_creator;

import org.h2gis.h2spatial.CreateSpatialExtension;
import org.h2gis.h2spatial.ut.SpatialH2UT;
import org.h2gis.utilities.GraphConstants;
import org.junit.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.h2gis.utilities.GraphConstants.EDGE_CENT_SUFFIX;
import static org.h2gis.utilities.GraphConstants.NODE_CENT_SUFFIX;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Adam Gouge
 */
public class ST_GraphAnalysisTest {

    private static Connection connection;
    private Statement st;
    private static final double TOLERANCE = 0.0;
    private static final String DO = "'directed - edge_orientation'";
    private static final String RO = "'reversed - edge_orientation'";
    private static final String U = "'undirected'";
    private static final String W = "'weight'";

    @BeforeClass
    public static void setUp() throws Exception {
        // Keep a connection alive to not close the DataBase on each unit test
        connection = SpatialH2UT.createSpatialDataBase("ST_GraphAnalysisTest", true);
        CreateSpatialExtension.registerFunction(connection.createStatement(), new ST_GraphAnalysis(), "");
        GraphCreatorTest.registerCormenGraph(connection);
    }

    @Before
    public void setUpStatement() throws Exception {
        st = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
    }

    @After
    public void tearDownStatement() throws Exception {
        st.close();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        connection.close();
    }

    @Test
    public void DO() throws Exception {
        st.execute("DROP TABLE IF EXISTS CORMEN_EDGES_ALL" + NODE_CENT_SUFFIX);
        st.execute("DROP TABLE IF EXISTS CORMEN_EDGES_ALL" + EDGE_CENT_SUFFIX);

        // SELECT * FROM ST_GraphAnalysis('CORMEN_EDGES_ALL',
        //     'directed - edge_orientation')
        final ResultSet rs = compute(DO);
        assertTrue(rs.next());
        assertTrue(rs.getBoolean(1));
        assertFalse(rs.next());

        final ResultSet nodeCent = st.executeQuery("SELECT * FROM CORMEN_EDGES_ALL" + NODE_CENT_SUFFIX);
        checkNodes(nodeCent,
                new double[]{
                        4.0 / (0.0 + 1.0 + 1.0 + 2.0 + 1.0),
                        4.0 / (3.0 + 0.0 + 1.0 + 2.0 + 2.0),
                        4.0 / (2.0 + 1.0 + 0.0 + 1.0 + 1.0),
                        4.0 / (2.0 + 1.0 + 2.0 + 0.0 + 1.0),
                        4.0 / (1.0 + 2.0 + 2.0 + 1.0 + 0.0)}
        );
        final ResultSet edgeCent = st.executeQuery("SELECT * FROM CORMEN_EDGES_ALL" + EDGE_CENT_SUFFIX);
    }

    @Test
    public void WDO() throws Exception {
        st.execute("DROP TABLE IF EXISTS CORMEN_EDGES_ALL" + NODE_CENT_SUFFIX);
        st.execute("DROP TABLE IF EXISTS CORMEN_EDGES_ALL" + EDGE_CENT_SUFFIX);

        // SELECT * FROM ST_GraphAnalysis('CORMEN_EDGES_ALL',
        //     'directed - edge_orientation', 'weight')
        final ResultSet rs = compute(DO, W);
        assertTrue(rs.next());
        assertTrue(rs.getBoolean(1));
        assertFalse(rs.next());

        final ResultSet nodeCent = st.executeQuery("SELECT * FROM CORMEN_EDGES_ALL" + NODE_CENT_SUFFIX);
        checkNodes(nodeCent,
                new double[]{
                        4.0 / (0.0 + 8.0 + 5.0 + 13.0 + 7.0),
                        4.0 / (11.0 + 0.0 + 2.0 + 10.0 + 4.0),
                        4.0 / (9.0 + 3.0 + 0.0 + 8.0 + 2.0),
                        4.0 / (11.0 + 1.0 + 3.0 + 0.0 + 4.0),
                        4.0 / (7.0 + 7.0 + 9.0 + 6.0 + 0.0)}
        );

        final ResultSet edgeCent = st.executeQuery("SELECT * FROM CORMEN_EDGES_ALL" + EDGE_CENT_SUFFIX);
    }

    @Test
    public void RO() throws Exception {
        st.execute("DROP TABLE IF EXISTS CORMEN_EDGES_ALL" + NODE_CENT_SUFFIX);
        st.execute("DROP TABLE IF EXISTS CORMEN_EDGES_ALL" + EDGE_CENT_SUFFIX);

        // SELECT * FROM ST_GraphAnalysis('CORMEN_EDGES_ALL',
        //     'reversed - edge_orientation')
        final ResultSet rs = compute(RO);
        assertTrue(rs.next());
        assertTrue(rs.getBoolean(1));
        assertFalse(rs.next());

        final ResultSet nodeCent = st.executeQuery("SELECT * FROM CORMEN_EDGES_ALL" + NODE_CENT_SUFFIX);
        checkNodes(nodeCent,
                new double[]{
                        4.0 / (0.0 + 3.0 + 2.0 + 2.0 + 1.0),
                        4.0 / (1.0 + 0.0 + 1.0 + 1.0 + 2.0),
                        4.0 / (1.0 + 1.0 + 0.0 + 2.0 + 2.0),
                        4.0 / (2.0 + 2.0 + 1.0 + 0.0 + 1.0),
                        4.0 / (1.0 + 2.0 + 1.0 + 1.0 + 0.0)}
        );
    }

    @Test
    public void WRO() throws Exception {
        st.execute("DROP TABLE IF EXISTS CORMEN_EDGES_ALL" + NODE_CENT_SUFFIX);
        st.execute("DROP TABLE IF EXISTS CORMEN_EDGES_ALL" + EDGE_CENT_SUFFIX);

        // SELECT * FROM ST_GraphAnalysis('CORMEN_EDGES_ALL',
        //     'reversed - edge_orientation', 'weight')
        final ResultSet rs = compute(RO, W);
        assertTrue(rs.next());
        assertTrue(rs.getBoolean(1));
        assertFalse(rs.next());

        final ResultSet nodeCent = st.executeQuery("SELECT * FROM CORMEN_EDGES_ALL" + NODE_CENT_SUFFIX);
        checkNodes(nodeCent,
                new double[]{
                        4.0 / (0.0 + 11.0 + 9.0 + 11.0 + 7.0),
                        4.0 / (8.0 + 0.0 + 3.0 + 1.0 + 7.0),
                        4.0 / (5.0 + 2.0 + 0.0 + 3.0 + 9.0),
                        4.0 / (13.0 + 10.0 + 8.0 + 0.0 + 6.0),
                        4.0 / (7.0 + 4.0 + 2.0 + 4.0 + 0.0)}
        );
    }

    @Test
    public void U() throws Exception {
        st.execute("DROP TABLE IF EXISTS CORMEN_EDGES_ALL" + NODE_CENT_SUFFIX);
        st.execute("DROP TABLE IF EXISTS CORMEN_EDGES_ALL" + EDGE_CENT_SUFFIX);

        // SELECT * FROM ST_GraphAnalysis('CORMEN_EDGES_ALL',
        //     'undirected')
        final ResultSet rs = compute(U);
        assertTrue(rs.next());
        assertTrue(rs.getBoolean(1));
        assertFalse(rs.next());

        final ResultSet nodeCent = st.executeQuery("SELECT * FROM CORMEN_EDGES_ALL" + NODE_CENT_SUFFIX);
       checkNodes(nodeCent,
               new double[]{
                        4.0 / (0.0 +  1.0 +  1.0 +  2.0 +  1.0),
                        4.0 / (1.0 +  0.0 +  1.0 +  1.0 +  2.0),
                        4.0 / (1.0 +  1.0 +  0.0 +  1.0 +  1.0),
                        4.0 / (2.0 +  1.0 +  1.0 +  0.0 +  1.0),
                        4.0 / (1.0 +  2.0 +  1.0 +  1.0 +  0.0)}
       );
    }

    @Test
    public void WU() throws Exception {
        st.execute("DROP TABLE IF EXISTS CORMEN_EDGES_ALL" + NODE_CENT_SUFFIX);
        st.execute("DROP TABLE IF EXISTS CORMEN_EDGES_ALL" + EDGE_CENT_SUFFIX);

        // SELECT * FROM ST_GraphAnalysis('CORMEN_EDGES_ALL',
        //     'undirected', 'weight')
        final ResultSet rs = compute(U, W);
        assertTrue(rs.next());
        assertTrue(rs.getBoolean(1));
        assertFalse(rs.next());

        final ResultSet nodeCent = st.executeQuery("SELECT * FROM CORMEN_EDGES_ALL" + NODE_CENT_SUFFIX);
       checkNodes(nodeCent,
               new double[]{
                        4.0 / (0.0 + 7.0 + 5.0 + 8.0 + 7.0),
                        4.0 / (7.0 + 0.0 + 2.0 + 1.0 + 4.0),
                        4.0 / (5.0 + 2.0 + 0.0 + 3.0 + 2.0),
                        4.0 / (8.0 + 1.0 + 3.0 + 0.0 + 4.0),
                        4.0 / (7.0 + 4.0 + 2.0 + 4.0 + 0.0)}
       );
    }

    private ResultSet compute(String orientation, String weight) throws SQLException {
        return st.executeQuery(
                "SELECT ST_GraphAnalysis('CORMEN_EDGES_ALL', "
                        + orientation + ((weight != null) ? ", " + weight : "") + ")");
    }

    private ResultSet compute(String orientation) throws SQLException {
        return compute(orientation, null);
    }

    private void checkNodes(ResultSet nodeCent, double[] closeness) throws SQLException {
        try {
            while (nodeCent.next()) {
                final int nodeID = nodeCent.getInt(GraphConstants.NODE_ID);
                // Check closeness.
                assertEquals(closeness[nodeID - 1], nodeCent.getDouble(GraphConstants.CLOSENESS), TOLERANCE);
            }
        } finally {
            nodeCent.close();
        }
    }
}
