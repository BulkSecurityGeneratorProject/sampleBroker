package com.arris.cloudng.wifibroker.web.rest;

import com.arris.cloudng.wifibroker.SampleBrokerApp;

import com.arris.cloudng.wifibroker.domain.WlanGroup;
import com.arris.cloudng.wifibroker.domain.Wlan;
import com.arris.cloudng.wifibroker.domain.Zone;
import com.arris.cloudng.wifibroker.repository.WlanGroupRepository;
import com.arris.cloudng.wifibroker.service.WlanGroupService;
import com.arris.cloudng.wifibroker.web.rest.errors.ExceptionTranslator;
import com.arris.cloudng.wifibroker.service.dto.WlanGroupCriteria;
import com.arris.cloudng.wifibroker.service.WlanGroupQueryService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static com.arris.cloudng.wifibroker.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the WlanGroupResource REST controller.
 *
 * @see WlanGroupResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SampleBrokerApp.class)
public class WlanGroupResourceIntTest {

    private static final String DEFAULT_DEVICE_ID = "AAAAAAAAAA";
    private static final String UPDATED_DEVICE_ID = "BBBBBBBBBB";

    private static final String DEFAULT_DEVICE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_DEVICE_NAME = "BBBBBBBBBB";

    @Autowired
    private WlanGroupRepository wlanGroupRepository;

    @Autowired
    private WlanGroupService wlanGroupService;

    @Autowired
    private WlanGroupQueryService wlanGroupQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restWlanGroupMockMvc;

    private WlanGroup wlanGroup;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final WlanGroupResource wlanGroupResource = new WlanGroupResource(wlanGroupService, wlanGroupQueryService);
        this.restWlanGroupMockMvc = MockMvcBuilders.standaloneSetup(wlanGroupResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WlanGroup createEntity(EntityManager em) {
        WlanGroup wlanGroup = new WlanGroup()
            .deviceId(DEFAULT_DEVICE_ID)
            .deviceName(DEFAULT_DEVICE_NAME);
        return wlanGroup;
    }

    @Before
    public void initTest() {
        wlanGroup = createEntity(em);
    }

    @Test
    @Transactional
    public void createWlanGroup() throws Exception {
        int databaseSizeBeforeCreate = wlanGroupRepository.findAll().size();

        // Create the WlanGroup
        restWlanGroupMockMvc.perform(post("/api/wlan-groups")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(wlanGroup)))
            .andExpect(status().isCreated());

        // Validate the WlanGroup in the database
        List<WlanGroup> wlanGroupList = wlanGroupRepository.findAll();
        assertThat(wlanGroupList).hasSize(databaseSizeBeforeCreate + 1);
        WlanGroup testWlanGroup = wlanGroupList.get(wlanGroupList.size() - 1);
        assertThat(testWlanGroup.getDeviceId()).isEqualTo(DEFAULT_DEVICE_ID);
        assertThat(testWlanGroup.getDeviceName()).isEqualTo(DEFAULT_DEVICE_NAME);
    }

    @Test
    @Transactional
    public void createWlanGroupWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = wlanGroupRepository.findAll().size();

        // Create the WlanGroup with an existing ID
        wlanGroup.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restWlanGroupMockMvc.perform(post("/api/wlan-groups")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(wlanGroup)))
            .andExpect(status().isBadRequest());

        // Validate the WlanGroup in the database
        List<WlanGroup> wlanGroupList = wlanGroupRepository.findAll();
        assertThat(wlanGroupList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkDeviceIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = wlanGroupRepository.findAll().size();
        // set the field null
        wlanGroup.setDeviceId(null);

        // Create the WlanGroup, which fails.

        restWlanGroupMockMvc.perform(post("/api/wlan-groups")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(wlanGroup)))
            .andExpect(status().isBadRequest());

        List<WlanGroup> wlanGroupList = wlanGroupRepository.findAll();
        assertThat(wlanGroupList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDeviceNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = wlanGroupRepository.findAll().size();
        // set the field null
        wlanGroup.setDeviceName(null);

        // Create the WlanGroup, which fails.

        restWlanGroupMockMvc.perform(post("/api/wlan-groups")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(wlanGroup)))
            .andExpect(status().isBadRequest());

        List<WlanGroup> wlanGroupList = wlanGroupRepository.findAll();
        assertThat(wlanGroupList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllWlanGroups() throws Exception {
        // Initialize the database
        wlanGroupRepository.saveAndFlush(wlanGroup);

        // Get all the wlanGroupList
        restWlanGroupMockMvc.perform(get("/api/wlan-groups?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(wlanGroup.getId().intValue())))
            .andExpect(jsonPath("$.[*].deviceId").value(hasItem(DEFAULT_DEVICE_ID.toString())))
            .andExpect(jsonPath("$.[*].deviceName").value(hasItem(DEFAULT_DEVICE_NAME.toString())));
    }

    @Test
    @Transactional
    public void getWlanGroup() throws Exception {
        // Initialize the database
        wlanGroupRepository.saveAndFlush(wlanGroup);

        // Get the wlanGroup
        restWlanGroupMockMvc.perform(get("/api/wlan-groups/{id}", wlanGroup.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(wlanGroup.getId().intValue()))
            .andExpect(jsonPath("$.deviceId").value(DEFAULT_DEVICE_ID.toString()))
            .andExpect(jsonPath("$.deviceName").value(DEFAULT_DEVICE_NAME.toString()));
    }

    @Test
    @Transactional
    public void getAllWlanGroupsByDeviceIdIsEqualToSomething() throws Exception {
        // Initialize the database
        wlanGroupRepository.saveAndFlush(wlanGroup);

        // Get all the wlanGroupList where deviceId equals to DEFAULT_DEVICE_ID
        defaultWlanGroupShouldBeFound("deviceId.equals=" + DEFAULT_DEVICE_ID);

        // Get all the wlanGroupList where deviceId equals to UPDATED_DEVICE_ID
        defaultWlanGroupShouldNotBeFound("deviceId.equals=" + UPDATED_DEVICE_ID);
    }

    @Test
    @Transactional
    public void getAllWlanGroupsByDeviceIdIsInShouldWork() throws Exception {
        // Initialize the database
        wlanGroupRepository.saveAndFlush(wlanGroup);

        // Get all the wlanGroupList where deviceId in DEFAULT_DEVICE_ID or UPDATED_DEVICE_ID
        defaultWlanGroupShouldBeFound("deviceId.in=" + DEFAULT_DEVICE_ID + "," + UPDATED_DEVICE_ID);

        // Get all the wlanGroupList where deviceId equals to UPDATED_DEVICE_ID
        defaultWlanGroupShouldNotBeFound("deviceId.in=" + UPDATED_DEVICE_ID);
    }

    @Test
    @Transactional
    public void getAllWlanGroupsByDeviceIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        wlanGroupRepository.saveAndFlush(wlanGroup);

        // Get all the wlanGroupList where deviceId is not null
        defaultWlanGroupShouldBeFound("deviceId.specified=true");

        // Get all the wlanGroupList where deviceId is null
        defaultWlanGroupShouldNotBeFound("deviceId.specified=false");
    }

    @Test
    @Transactional
    public void getAllWlanGroupsByDeviceNameIsEqualToSomething() throws Exception {
        // Initialize the database
        wlanGroupRepository.saveAndFlush(wlanGroup);

        // Get all the wlanGroupList where deviceName equals to DEFAULT_DEVICE_NAME
        defaultWlanGroupShouldBeFound("deviceName.equals=" + DEFAULT_DEVICE_NAME);

        // Get all the wlanGroupList where deviceName equals to UPDATED_DEVICE_NAME
        defaultWlanGroupShouldNotBeFound("deviceName.equals=" + UPDATED_DEVICE_NAME);
    }

    @Test
    @Transactional
    public void getAllWlanGroupsByDeviceNameIsInShouldWork() throws Exception {
        // Initialize the database
        wlanGroupRepository.saveAndFlush(wlanGroup);

        // Get all the wlanGroupList where deviceName in DEFAULT_DEVICE_NAME or UPDATED_DEVICE_NAME
        defaultWlanGroupShouldBeFound("deviceName.in=" + DEFAULT_DEVICE_NAME + "," + UPDATED_DEVICE_NAME);

        // Get all the wlanGroupList where deviceName equals to UPDATED_DEVICE_NAME
        defaultWlanGroupShouldNotBeFound("deviceName.in=" + UPDATED_DEVICE_NAME);
    }

    @Test
    @Transactional
    public void getAllWlanGroupsByDeviceNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        wlanGroupRepository.saveAndFlush(wlanGroup);

        // Get all the wlanGroupList where deviceName is not null
        defaultWlanGroupShouldBeFound("deviceName.specified=true");

        // Get all the wlanGroupList where deviceName is null
        defaultWlanGroupShouldNotBeFound("deviceName.specified=false");
    }

    @Test
    @Transactional
    public void getAllWlanGroupsByMembersIsEqualToSomething() throws Exception {
        // Initialize the database
        Wlan members = WlanResourceIntTest.createEntity(em);
        em.persist(members);
        em.flush();
        wlanGroup.addMembers(members);
        wlanGroupRepository.saveAndFlush(wlanGroup);
        Long membersId = members.getId();

        // Get all the wlanGroupList where members equals to membersId
        defaultWlanGroupShouldBeFound("membersId.equals=" + membersId);

        // Get all the wlanGroupList where members equals to membersId + 1
        defaultWlanGroupShouldNotBeFound("membersId.equals=" + (membersId + 1));
    }


    @Test
    @Transactional
    public void getAllWlanGroupsByZoneIsEqualToSomething() throws Exception {
        // Initialize the database
        Zone zone = ZoneResourceIntTest.createEntity(em);
        em.persist(zone);
        em.flush();
        wlanGroup.setZone(zone);
        wlanGroupRepository.saveAndFlush(wlanGroup);
        Long zoneId = zone.getId();

        // Get all the wlanGroupList where zone equals to zoneId
        defaultWlanGroupShouldBeFound("zoneId.equals=" + zoneId);

        // Get all the wlanGroupList where zone equals to zoneId + 1
        defaultWlanGroupShouldNotBeFound("zoneId.equals=" + (zoneId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultWlanGroupShouldBeFound(String filter) throws Exception {
        restWlanGroupMockMvc.perform(get("/api/wlan-groups?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(wlanGroup.getId().intValue())))
            .andExpect(jsonPath("$.[*].deviceId").value(hasItem(DEFAULT_DEVICE_ID.toString())))
            .andExpect(jsonPath("$.[*].deviceName").value(hasItem(DEFAULT_DEVICE_NAME.toString())));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultWlanGroupShouldNotBeFound(String filter) throws Exception {
        restWlanGroupMockMvc.perform(get("/api/wlan-groups?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @Transactional
    public void getNonExistingWlanGroup() throws Exception {
        // Get the wlanGroup
        restWlanGroupMockMvc.perform(get("/api/wlan-groups/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateWlanGroup() throws Exception {
        // Initialize the database
        wlanGroupService.save(wlanGroup);

        int databaseSizeBeforeUpdate = wlanGroupRepository.findAll().size();

        // Update the wlanGroup
        WlanGroup updatedWlanGroup = wlanGroupRepository.findOne(wlanGroup.getId());
        // Disconnect from session so that the updates on updatedWlanGroup are not directly saved in db
        em.detach(updatedWlanGroup);
        updatedWlanGroup
            .deviceId(UPDATED_DEVICE_ID)
            .deviceName(UPDATED_DEVICE_NAME);

        restWlanGroupMockMvc.perform(put("/api/wlan-groups")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedWlanGroup)))
            .andExpect(status().isOk());

        // Validate the WlanGroup in the database
        List<WlanGroup> wlanGroupList = wlanGroupRepository.findAll();
        assertThat(wlanGroupList).hasSize(databaseSizeBeforeUpdate);
        WlanGroup testWlanGroup = wlanGroupList.get(wlanGroupList.size() - 1);
        assertThat(testWlanGroup.getDeviceId()).isEqualTo(UPDATED_DEVICE_ID);
        assertThat(testWlanGroup.getDeviceName()).isEqualTo(UPDATED_DEVICE_NAME);
    }

    @Test
    @Transactional
    public void updateNonExistingWlanGroup() throws Exception {
        int databaseSizeBeforeUpdate = wlanGroupRepository.findAll().size();

        // Create the WlanGroup

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restWlanGroupMockMvc.perform(put("/api/wlan-groups")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(wlanGroup)))
            .andExpect(status().isCreated());

        // Validate the WlanGroup in the database
        List<WlanGroup> wlanGroupList = wlanGroupRepository.findAll();
        assertThat(wlanGroupList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteWlanGroup() throws Exception {
        // Initialize the database
        wlanGroupService.save(wlanGroup);

        int databaseSizeBeforeDelete = wlanGroupRepository.findAll().size();

        // Get the wlanGroup
        restWlanGroupMockMvc.perform(delete("/api/wlan-groups/{id}", wlanGroup.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<WlanGroup> wlanGroupList = wlanGroupRepository.findAll();
        assertThat(wlanGroupList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(WlanGroup.class);
        WlanGroup wlanGroup1 = new WlanGroup();
        wlanGroup1.setId(1L);
        WlanGroup wlanGroup2 = new WlanGroup();
        wlanGroup2.setId(wlanGroup1.getId());
        assertThat(wlanGroup1).isEqualTo(wlanGroup2);
        wlanGroup2.setId(2L);
        assertThat(wlanGroup1).isNotEqualTo(wlanGroup2);
        wlanGroup1.setId(null);
        assertThat(wlanGroup1).isNotEqualTo(wlanGroup2);
    }
}
