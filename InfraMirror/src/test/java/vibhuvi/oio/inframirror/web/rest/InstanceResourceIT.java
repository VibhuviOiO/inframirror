package vibhuvi.oio.inframirror.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static vibhuvi.oio.inframirror.domain.InstanceAsserts.*;
import static vibhuvi.oio.inframirror.web.rest.TestUtil.createUpdateProxyForBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import vibhuvi.oio.inframirror.IntegrationTest;
import vibhuvi.oio.inframirror.domain.Agent;
import vibhuvi.oio.inframirror.domain.Datacenter;
import vibhuvi.oio.inframirror.domain.Instance;
import vibhuvi.oio.inframirror.repository.InstanceRepository;
import vibhuvi.oio.inframirror.service.dto.InstanceDTO;
import vibhuvi.oio.inframirror.service.mapper.InstanceMapper;
import vibhuvi.oio.inframirror.domain.enumeration.InstanceType;
import vibhuvi.oio.inframirror.domain.enumeration.OperatingSystem;

/**
 * Integration tests for the {@link InstanceResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class InstanceResourceIT extends AbstractEntityResourceIT<Instance, InstanceRepository> {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_HOSTNAME = "AAAAAAAAAA";
    private static final String UPDATED_HOSTNAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final InstanceType DEFAULT_INSTANCE_TYPE = InstanceType.VM;
    private static final InstanceType UPDATED_INSTANCE_TYPE = InstanceType.BARE_METAL;

    private static final OperatingSystem DEFAULT_OPERATING_SYSTEM = OperatingSystem.LINUX;
    private static final OperatingSystem UPDATED_OPERATING_SYSTEM = OperatingSystem.WINDOWS;

    private static final String DEFAULT_PLATFORM = "AAAAAAAAAA";
    private static final String UPDATED_PLATFORM = "BBBBBBBBBB";

    private static final String DEFAULT_PRIVATE_IP_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_PRIVATE_IP_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_PUBLIC_IP_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_PUBLIC_IP_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_TAGS = "AAAAAAAAAA";
    private static final String UPDATED_TAGS = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/instances";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/instances/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private InstanceRepository instanceRepository;

    @Autowired
    private InstanceMapper instanceMapper;

    private Instance instance;
    private Instance insertedInstance;

    @Override
    protected InstanceRepository getRepository() {
        return instanceRepository;
    }

    @Override
    protected String getEntityApiUrl() {
        return ENTITY_API_URL;
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Instance createEntity(EntityManager em) {
        Instance instance = new Instance()
            .name(DEFAULT_NAME)
            .hostname(DEFAULT_HOSTNAME)
            .description(DEFAULT_DESCRIPTION)
            .instanceType(DEFAULT_INSTANCE_TYPE)
            .operatingSystem(DEFAULT_OPERATING_SYSTEM)
            .platform(DEFAULT_PLATFORM)
            .privateIpAddress(DEFAULT_PRIVATE_IP_ADDRESS)
            .publicIpAddress(DEFAULT_PUBLIC_IP_ADDRESS)
            .tags(DEFAULT_TAGS)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
        // Add required entity
        Datacenter datacenter;
        if (TestUtil.findAll(em, Datacenter.class).isEmpty()) {
            datacenter = DatacenterResourceIT.createEntity();
            em.persist(datacenter);
            em.flush();
        } else {
            datacenter = TestUtil.findAll(em, Datacenter.class).get(0);
        }
        instance.setDatacenter(datacenter);
        return instance;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Instance createUpdatedEntity(EntityManager em) {
        Instance updatedInstance = new Instance()
            .name(UPDATED_NAME)
            .hostname(UPDATED_HOSTNAME)
            .description(UPDATED_DESCRIPTION)
            .instanceType(UPDATED_INSTANCE_TYPE)
            .operatingSystem(UPDATED_OPERATING_SYSTEM)
            .platform(UPDATED_PLATFORM)
            .privateIpAddress(UPDATED_PRIVATE_IP_ADDRESS)
            .publicIpAddress(UPDATED_PUBLIC_IP_ADDRESS)
            .tags(UPDATED_TAGS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        // Add required entity
        Datacenter datacenter;
        if (TestUtil.findAll(em, Datacenter.class).isEmpty()) {
            datacenter = DatacenterResourceIT.createUpdatedEntity();
            em.persist(datacenter);
            em.flush();
        } else {
            datacenter = TestUtil.findAll(em, Datacenter.class).get(0);
        }
        updatedInstance.setDatacenter(datacenter);
        return updatedInstance;
    }

    @BeforeEach
    void initTest() {
        instance = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedInstance != null) {
            instanceRepository.delete(insertedInstance);
            insertedInstance = null;
        }
    }

    void createInstance() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Instance
        InstanceDTO instanceDTO = instanceMapper.toDto(instance);
        var returnedInstanceDTO = om.readValue(
            restMockMvc
                .perform(
                    post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(instanceDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            InstanceDTO.class
        );

        // Validate the Instance in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedInstance = instanceMapper.toEntity(returnedInstanceDTO);
        assertInstanceUpdatableFieldsEquals(returnedInstance, getPersistedEntity(returnedInstance.getId()));


        insertedInstance = returnedInstance;
    }

    void createInstanceWithExistingId() throws Exception {
        // Create the Instance with an existing ID
        instance.setId(1L);
        InstanceDTO instanceDTO = instanceMapper.toDto(instance);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(instanceDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Instance in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        instance.setName(null);

        // Create the Instance, which fails.
        InstanceDTO instanceDTO = instanceMapper.toDto(instance);

        restMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(instanceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

    }

    void checkHostnameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        instance.setHostname(null);

        // Create the Instance, which fails.
        InstanceDTO instanceDTO = instanceMapper.toDto(instance);

        restMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(instanceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

    }

    void checkInstanceTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        instance.setInstanceType(null);

        // Create the Instance, which fails.
        InstanceDTO instanceDTO = instanceMapper.toDto(instance);

        restMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(instanceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

    }

    void getAllInstances() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList
        restMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(instance.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].hostname").value(hasItem(DEFAULT_HOSTNAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].instanceType").value(hasItem(DEFAULT_INSTANCE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].operatingSystem").value(hasItem(DEFAULT_OPERATING_SYSTEM.toString())))
            .andExpect(jsonPath("$.[*].platform").value(hasItem(DEFAULT_PLATFORM)))
            .andExpect(jsonPath("$.[*].privateIpAddress").value(hasItem(DEFAULT_PRIVATE_IP_ADDRESS)))
            .andExpect(jsonPath("$.[*].publicIpAddress").value(hasItem(DEFAULT_PUBLIC_IP_ADDRESS)))
            .andExpect(jsonPath("$.[*].tags").value(hasItem(DEFAULT_TAGS)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    void getInstance() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get the instance
        restMockMvc
            .perform(get(ENTITY_API_URL_ID, instance.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(instance.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.hostname").value(DEFAULT_HOSTNAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.instanceType").value(DEFAULT_INSTANCE_TYPE.toString()))
            .andExpect(jsonPath("$.operatingSystem").value(DEFAULT_OPERATING_SYSTEM.toString()))
            .andExpect(jsonPath("$.platform").value(DEFAULT_PLATFORM))
            .andExpect(jsonPath("$.privateIpAddress").value(DEFAULT_PRIVATE_IP_ADDRESS))
            .andExpect(jsonPath("$.publicIpAddress").value(DEFAULT_PUBLIC_IP_ADDRESS))
            .andExpect(jsonPath("$.tags").value(DEFAULT_TAGS))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    void getInstancesByIdFiltering() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        Long id = instance.getId();

        defaultInstanceFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultInstanceFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultInstanceFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    void getAllInstancesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where name equals to
        defaultInstanceFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    void getAllInstancesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where name in
        defaultInstanceFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    void getAllInstancesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where name is not null
        defaultInstanceFiltering("name.specified=true", "name.specified=false");
    }

    void getAllInstancesByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where name contains
        defaultInstanceFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    void getAllInstancesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where name does not contain
        defaultInstanceFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    void getAllInstancesByHostnameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where hostname equals to
        defaultInstanceFiltering("hostname.equals=" + DEFAULT_HOSTNAME, "hostname.equals=" + UPDATED_HOSTNAME);
    }

    void getAllInstancesByHostnameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where hostname in
        defaultInstanceFiltering("hostname.in=" + DEFAULT_HOSTNAME + "," + UPDATED_HOSTNAME, "hostname.in=" + UPDATED_HOSTNAME);
    }

    void getAllInstancesByHostnameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where hostname is not null
        defaultInstanceFiltering("hostname.specified=true", "hostname.specified=false");
    }

    void getAllInstancesByHostnameContainsSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where hostname contains
        defaultInstanceFiltering("hostname.contains=" + DEFAULT_HOSTNAME, "hostname.contains=" + UPDATED_HOSTNAME);
    }

    void getAllInstancesByHostnameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where hostname does not contain
        defaultInstanceFiltering("hostname.doesNotContain=" + UPDATED_HOSTNAME, "hostname.doesNotContain=" + DEFAULT_HOSTNAME);
    }

    void getAllInstancesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where description equals to
        defaultInstanceFiltering("description.equals=" + DEFAULT_DESCRIPTION, "description.equals=" + UPDATED_DESCRIPTION);
    }

    void getAllInstancesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where description in
        defaultInstanceFiltering(
            "description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION,
            "description.in=" + UPDATED_DESCRIPTION
        );
    }

    void getAllInstancesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where description is not null
        defaultInstanceFiltering("description.specified=true", "description.specified=false");
    }

    void getAllInstancesByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where description contains
        defaultInstanceFiltering("description.contains=" + DEFAULT_DESCRIPTION, "description.contains=" + UPDATED_DESCRIPTION);
    }

    void getAllInstancesByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where description does not contain
        defaultInstanceFiltering("description.doesNotContain=" + UPDATED_DESCRIPTION, "description.doesNotContain=" + DEFAULT_DESCRIPTION);
    }

    void getAllInstancesByInstanceTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where instanceType equals to
        defaultInstanceFiltering("instanceType.equals=" + DEFAULT_INSTANCE_TYPE, "instanceType.equals=" + UPDATED_INSTANCE_TYPE);
    }

    void getAllInstancesByInstanceTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where instanceType in
        defaultInstanceFiltering(
            "instanceType.in=" + DEFAULT_INSTANCE_TYPE + "," + UPDATED_INSTANCE_TYPE,
            "instanceType.in=" + UPDATED_INSTANCE_TYPE
        );
    }

    void getAllInstancesByInstanceTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where instanceType is not null
        defaultInstanceFiltering("instanceType.specified=true", "instanceType.specified=false");
    }

    void getAllInstancesByInstanceTypeContainsSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Enum fields don't support contains operation, use equals instead
        defaultInstanceFiltering("instanceType.equals=" + DEFAULT_INSTANCE_TYPE, "instanceType.equals=" + UPDATED_INSTANCE_TYPE);
    }

    void getAllInstancesByInstanceTypeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Enum fields don't support doesNotContain operation, use notEquals instead
        defaultInstanceFiltering(
            "instanceType.notEquals=" + UPDATED_INSTANCE_TYPE,
            "instanceType.notEquals=" + DEFAULT_INSTANCE_TYPE
        );
    }






    void getAllInstancesByOperatingSystemIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where operatingSystem equals to
        defaultInstanceFiltering(
            "operatingSystem.equals=" + DEFAULT_OPERATING_SYSTEM,
            "operatingSystem.equals=" + UPDATED_OPERATING_SYSTEM
        );
    }

    void getAllInstancesByOperatingSystemIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where operatingSystem in
        defaultInstanceFiltering(
            "operatingSystem.in=" + DEFAULT_OPERATING_SYSTEM + "," + UPDATED_OPERATING_SYSTEM,
            "operatingSystem.in=" + UPDATED_OPERATING_SYSTEM
        );
    }

    void getAllInstancesByOperatingSystemIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where operatingSystem is not null
        defaultInstanceFiltering("operatingSystem.specified=true", "operatingSystem.specified=false");
    }

    void getAllInstancesByOperatingSystemContainsSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Enum fields don't support contains operation, use equals instead
        defaultInstanceFiltering(
            "operatingSystem.equals=" + DEFAULT_OPERATING_SYSTEM,
            "operatingSystem.equals=" + UPDATED_OPERATING_SYSTEM
        );
    }

    void getAllInstancesByOperatingSystemNotContainsSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Enum fields don't support doesNotContain operation, use notEquals instead
        defaultInstanceFiltering(
            "operatingSystem.notEquals=" + UPDATED_OPERATING_SYSTEM,
            "operatingSystem.notEquals=" + DEFAULT_OPERATING_SYSTEM
        );
    }

    void getAllInstancesByPlatformIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where platform equals to
        defaultInstanceFiltering("platform.equals=" + DEFAULT_PLATFORM, "platform.equals=" + UPDATED_PLATFORM);
    }

    void getAllInstancesByPlatformIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where platform in
        defaultInstanceFiltering("platform.in=" + DEFAULT_PLATFORM + "," + UPDATED_PLATFORM, "platform.in=" + UPDATED_PLATFORM);
    }

    void getAllInstancesByPlatformIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where platform is not null
        defaultInstanceFiltering("platform.specified=true", "platform.specified=false");
    }

    void getAllInstancesByPlatformContainsSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where platform contains
        defaultInstanceFiltering("platform.contains=" + DEFAULT_PLATFORM, "platform.contains=" + UPDATED_PLATFORM);
    }

    void getAllInstancesByPlatformNotContainsSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where platform does not contain
        defaultInstanceFiltering("platform.doesNotContain=" + UPDATED_PLATFORM, "platform.doesNotContain=" + DEFAULT_PLATFORM);
    }

    void getAllInstancesByPrivateIpAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where privateIpAddress equals to
        defaultInstanceFiltering(
            "privateIpAddress.equals=" + DEFAULT_PRIVATE_IP_ADDRESS,
            "privateIpAddress.equals=" + UPDATED_PRIVATE_IP_ADDRESS
        );
    }

    void getAllInstancesByPrivateIpAddressIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where privateIpAddress in
        defaultInstanceFiltering(
            "privateIpAddress.in=" + DEFAULT_PRIVATE_IP_ADDRESS + "," + UPDATED_PRIVATE_IP_ADDRESS,
            "privateIpAddress.in=" + UPDATED_PRIVATE_IP_ADDRESS
        );
    }

    void getAllInstancesByPrivateIpAddressIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where privateIpAddress is not null
        defaultInstanceFiltering("privateIpAddress.specified=true", "privateIpAddress.specified=false");
    }

    void getAllInstancesByPrivateIpAddressContainsSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where privateIpAddress contains
        defaultInstanceFiltering(
            "privateIpAddress.contains=" + DEFAULT_PRIVATE_IP_ADDRESS,
            "privateIpAddress.contains=" + UPDATED_PRIVATE_IP_ADDRESS
        );
    }

    void getAllInstancesByPrivateIpAddressNotContainsSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where privateIpAddress does not contain
        defaultInstanceFiltering(
            "privateIpAddress.doesNotContain=" + UPDATED_PRIVATE_IP_ADDRESS,
            "privateIpAddress.doesNotContain=" + DEFAULT_PRIVATE_IP_ADDRESS
        );
    }

    void getAllInstancesByPublicIpAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where publicIpAddress equals to
        defaultInstanceFiltering(
            "publicIpAddress.equals=" + DEFAULT_PUBLIC_IP_ADDRESS,
            "publicIpAddress.equals=" + UPDATED_PUBLIC_IP_ADDRESS
        );
    }

    void getAllInstancesByPublicIpAddressIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where publicIpAddress in
        defaultInstanceFiltering(
            "publicIpAddress.in=" + DEFAULT_PUBLIC_IP_ADDRESS + "," + UPDATED_PUBLIC_IP_ADDRESS,
            "publicIpAddress.in=" + UPDATED_PUBLIC_IP_ADDRESS
        );
    }

    void getAllInstancesByPublicIpAddressIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where publicIpAddress is not null
        defaultInstanceFiltering("publicIpAddress.specified=true", "publicIpAddress.specified=false");
    }

    void getAllInstancesByPublicIpAddressContainsSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where publicIpAddress contains
        defaultInstanceFiltering(
            "publicIpAddress.contains=" + DEFAULT_PUBLIC_IP_ADDRESS,
            "publicIpAddress.contains=" + UPDATED_PUBLIC_IP_ADDRESS
        );
    }

    void getAllInstancesByPublicIpAddressNotContainsSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where publicIpAddress does not contain
        defaultInstanceFiltering(
            "publicIpAddress.doesNotContain=" + UPDATED_PUBLIC_IP_ADDRESS,
            "publicIpAddress.doesNotContain=" + DEFAULT_PUBLIC_IP_ADDRESS
        );
    }













































































    void getAllInstancesByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where createdAt equals to
        defaultInstanceFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    void getAllInstancesByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where createdAt in
        defaultInstanceFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    void getAllInstancesByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where createdAt is not null
        defaultInstanceFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    void getAllInstancesByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where updatedAt equals to
        defaultInstanceFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    void getAllInstancesByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where updatedAt in
        defaultInstanceFiltering("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT, "updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    void getAllInstancesByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        // Get all the instanceList where updatedAt is not null
        defaultInstanceFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }







    void getAllInstancesByDatacenterIsEqualToSomething() throws Exception {
        Datacenter datacenter;
        if (TestUtil.findAll(em, Datacenter.class).isEmpty()) {
            instanceRepository.saveAndFlush(instance);
            datacenter = DatacenterResourceIT.createEntity();
        } else {
            datacenter = TestUtil.findAll(em, Datacenter.class).get(0);
        }
        em.persist(datacenter);
        em.flush();
        instance.setDatacenter(datacenter);
        instanceRepository.saveAndFlush(instance);
        Long datacenterId = datacenter.getId();
        // Get all the instanceList where datacenter equals to datacenterId
        defaultInstanceShouldBeFound("datacenterId.equals=" + datacenterId);

        // Get all the instanceList where datacenter equals to (datacenterId + 1)
        defaultInstanceShouldNotBeFound("datacenterId.equals=" + (datacenterId + 1));
    }

    void getAllInstancesByAgentIsEqualToSomething() throws Exception {
        Agent agent;
        if (TestUtil.findAll(em, Agent.class).isEmpty()) {
            instanceRepository.saveAndFlush(instance);
            agent = AgentResourceIT.createEntity();
        } else {
            agent = TestUtil.findAll(em, Agent.class).get(0);
        }
        em.persist(agent);
        em.flush();
        instance.setAgent(agent);
        instanceRepository.saveAndFlush(instance);
        Long agentId = agent.getId();
        // Get all the instanceList where agent equals to agentId
        defaultInstanceShouldBeFound("agentId.equals=" + agentId);

        // Get all the instanceList where agent equals to (agentId + 1)
        defaultInstanceShouldNotBeFound("agentId.equals=" + (agentId + 1));
    }

    private void defaultInstanceFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultInstanceShouldBeFound(shouldBeFound);
        defaultInstanceShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultInstanceShouldBeFound(String filter) throws Exception {
        restMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(instance.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].hostname").value(hasItem(DEFAULT_HOSTNAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].instanceType").value(hasItem(DEFAULT_INSTANCE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].operatingSystem").value(hasItem(DEFAULT_OPERATING_SYSTEM.toString())))
            .andExpect(jsonPath("$.[*].platform").value(hasItem(DEFAULT_PLATFORM)))
            .andExpect(jsonPath("$.[*].privateIpAddress").value(hasItem(DEFAULT_PRIVATE_IP_ADDRESS)))
            .andExpect(jsonPath("$.[*].publicIpAddress").value(hasItem(DEFAULT_PUBLIC_IP_ADDRESS)))
            .andExpect(jsonPath("$.[*].tags").value(hasItem(DEFAULT_TAGS)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));

        // Check, that the count call also returns 1
        restMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultInstanceShouldNotBeFound(String filter) throws Exception {
        restMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    void getNonExistingInstance() throws Exception {
        // Get the instance
        restMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    void putExistingInstance() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the instance
        Instance updatedInstance = instanceRepository.findById(instance.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedInstance are not directly saved in db
        em.detach(updatedInstance);
        updatedInstance
            .name(UPDATED_NAME)
            .hostname(UPDATED_HOSTNAME)
            .description(UPDATED_DESCRIPTION)
            .instanceType(UPDATED_INSTANCE_TYPE)
            .operatingSystem(UPDATED_OPERATING_SYSTEM)
            .platform(UPDATED_PLATFORM)
            .privateIpAddress(UPDATED_PRIVATE_IP_ADDRESS)
            .publicIpAddress(UPDATED_PUBLIC_IP_ADDRESS)
            .tags(UPDATED_TAGS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        InstanceDTO instanceDTO = instanceMapper.toDto(updatedInstance);

        restMockMvc
            .perform(
                put(ENTITY_API_URL_ID, instanceDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(instanceDTO))
            )
            .andExpect(status().isOk());

        // Validate the Instance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInstanceAllPropertiesEquals(updatedInstance, getPersistedEntity(updatedInstance.getId()));

    }

    void putNonExistingInstance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        instance.setId(longCount.incrementAndGet());

        // Create the Instance
        InstanceDTO instanceDTO = instanceMapper.toDto(instance);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMockMvc
            .perform(
                put(ENTITY_API_URL_ID, instanceDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(instanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Instance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    void putWithIdMismatchInstance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        instance.setId(longCount.incrementAndGet());

        // Create the Instance
        InstanceDTO instanceDTO = instanceMapper.toDto(instance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(instanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Instance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    void putWithMissingIdPathParamInstance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        instance.setId(longCount.incrementAndGet());

        // Create the Instance
        InstanceDTO instanceDTO = instanceMapper.toDto(instance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(instanceDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Instance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    void partialUpdateInstanceWithPatch() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the instance using partial update
        Instance partialUpdatedInstance = new Instance();
        partialUpdatedInstance.setId(instance.getId());

        partialUpdatedInstance
            .name(UPDATED_NAME)
            .hostname(UPDATED_HOSTNAME)
            .operatingSystem(UPDATED_OPERATING_SYSTEM)
            .createdAt(UPDATED_CREATED_AT);

        restMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInstance.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInstance))
            )
            .andExpect(status().isOk());

        // Validate the Instance in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInstanceUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedInstance, instance), getPersistedEntity(instance.getId()));
    }

    void fullUpdateInstanceWithPatch() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the instance using partial update
        Instance partialUpdatedInstance = new Instance();
        partialUpdatedInstance.setId(instance.getId());

        partialUpdatedInstance
            .name(UPDATED_NAME)
            .hostname(UPDATED_HOSTNAME)
            .description(UPDATED_DESCRIPTION)
            .instanceType(UPDATED_INSTANCE_TYPE)
            .operatingSystem(UPDATED_OPERATING_SYSTEM)
            .platform(UPDATED_PLATFORM)
            .privateIpAddress(UPDATED_PRIVATE_IP_ADDRESS)
            .publicIpAddress(UPDATED_PUBLIC_IP_ADDRESS)
            .tags(UPDATED_TAGS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInstance.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInstance))
            )
            .andExpect(status().isOk());

        // Validate the Instance in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInstanceUpdatableFieldsEquals(partialUpdatedInstance, getPersistedEntity(partialUpdatedInstance.getId()));
    }

    void patchNonExistingInstance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        instance.setId(longCount.incrementAndGet());

        // Create the Instance
        InstanceDTO instanceDTO = instanceMapper.toDto(instance);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, instanceDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(instanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Instance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    void patchWithIdMismatchInstance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        instance.setId(longCount.incrementAndGet());

        // Create the Instance
        InstanceDTO instanceDTO = instanceMapper.toDto(instance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(instanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Instance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    void patchWithMissingIdPathParamInstance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        instance.setId(longCount.incrementAndGet());

        // Create the Instance
        InstanceDTO instanceDTO = instanceMapper.toDto(instance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(instanceDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Instance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    void deleteInstance() throws Exception {
        // Initialize the database
        insertedInstance = instanceRepository.saveAndFlush(instance);
        instanceRepository.save(instance);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the instance
        restMockMvc
            .perform(delete(ENTITY_API_URL_ID, instance.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    void searchInstance() throws Exception {
        insertedInstance = instanceRepository.saveAndFlush(instance);
        flushAndClear();
        SearchTestHelper.testFullTextSearch(restMockMvc, ENTITY_SEARCH_API_URL, DEFAULT_NAME.substring(0, 3));
    }

    void searchInstancePrefix() throws Exception {
        insertedInstance = instanceRepository.saveAndFlush(instance);
        flushAndClear();
        SearchTestHelper.testPrefixSearch(restMockMvc, ENTITY_SEARCH_API_URL, DEFAULT_NAME.substring(0, 2));
    }

    void searchInstanceFuzzy() throws Exception {
        insertedInstance = instanceRepository.saveAndFlush(instance);
        flushAndClear();
        SearchTestHelper.testFuzzySearch(restMockMvc, ENTITY_SEARCH_API_URL, DEFAULT_NAME);
    }

    void searchInstanceWithHighlight() throws Exception {
        insertedInstance = instanceRepository.saveAndFlush(instance);
        flushAndClear();
        SearchTestHelper.testHighlightSearch(restMockMvc, ENTITY_SEARCH_API_URL, DEFAULT_NAME.substring(0, 3));
    }

    void searchInstanceEmptyQuery() throws Exception {
        insertedInstance = instanceRepository.saveAndFlush(instance);
        flushAndClear();
        SearchTestHelper.testEmptyQuerySearch(restMockMvc, ENTITY_SEARCH_API_URL);
    }

}
