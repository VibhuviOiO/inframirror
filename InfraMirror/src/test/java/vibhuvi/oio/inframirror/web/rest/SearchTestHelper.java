package vibhuvi.oio.inframirror.web.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Reusable helper methods for testing search endpoints.
 */
public class SearchTestHelper {

    /**
     * Test full-text search endpoint.
     *
     * @param mockMvc MockMvc instance
     * @param searchUrl Search endpoint URL (e.g., "/api/regions/_search")
     * @param query Search query
     * @throws Exception if test fails
     */
    public static void testFullTextSearch(MockMvc mockMvc, String searchUrl, String query) throws Exception {
        mockMvc
            .perform(get(searchUrl + "?query=" + query))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    /**
     * Test prefix search endpoint.
     *
     * @param mockMvc MockMvc instance
     * @param searchUrl Search endpoint URL (e.g., "/api/regions/_search/prefix")
     * @param query Search query
     * @throws Exception if test fails
     */
    public static void testPrefixSearch(MockMvc mockMvc, String searchUrl, String query) throws Exception {
        mockMvc
            .perform(get(searchUrl + "/prefix?query=" + query))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    /**
     * Test fuzzy search endpoint.
     *
     * @param mockMvc MockMvc instance
     * @param searchUrl Search endpoint URL (e.g., "/api/regions/_search/fuzzy")
     * @param query Search query
     * @throws Exception if test fails
     */
    public static void testFuzzySearch(MockMvc mockMvc, String searchUrl, String query) throws Exception {
        mockMvc
            .perform(get(searchUrl + "/fuzzy?query=" + query))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    /**
     * Test search with highlighting.
     *
     * @param mockMvc MockMvc instance
     * @param searchUrl Search endpoint URL (e.g., "/api/regions/_search/highlight")
     * @param query Search query
     * @throws Exception if test fails
     */
    public static void testHighlightSearch(MockMvc mockMvc, String searchUrl, String query) throws Exception {
        mockMvc
            .perform(get(searchUrl + "/highlight?query=" + query))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    /**
     * Test empty query search (should return all results).
     *
     * @param mockMvc MockMvc instance
     * @param searchUrl Search endpoint URL
     * @throws Exception if test fails
     */
    public static void testEmptyQuerySearch(MockMvc mockMvc, String searchUrl) throws Exception {
        mockMvc
            .perform(get(searchUrl + "?query="))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }
}
