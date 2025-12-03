package vibhuvi.oio.inframirror.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vibhuvi.oio.inframirror.domain.Schedule;
import vibhuvi.oio.inframirror.repository.ScheduleRepository;
import vibhuvi.oio.inframirror.repository.search.ScheduleSearchRepository;
import vibhuvi.oio.inframirror.service.ScheduleService;
import vibhuvi.oio.inframirror.service.dto.ScheduleDTO;
import vibhuvi.oio.inframirror.service.mapper.ScheduleMapper;

/**
 * Service Implementation for managing {@link vibhuvi.oio.inframirror.domain.Schedule}.
 */
@Service
@Transactional
public class ScheduleServiceImpl implements ScheduleService {

    private static final Logger LOG = LoggerFactory.getLogger(ScheduleServiceImpl.class);

    private final ScheduleRepository scheduleRepository;

    private final ScheduleMapper scheduleMapper;

    private final ScheduleSearchRepository scheduleSearchRepository;

    public ScheduleServiceImpl(
        ScheduleRepository scheduleRepository,
        ScheduleMapper scheduleMapper,
        ScheduleSearchRepository scheduleSearchRepository
    ) {
        this.scheduleRepository = scheduleRepository;
        this.scheduleMapper = scheduleMapper;
        this.scheduleSearchRepository = scheduleSearchRepository;
    }

    @Override
    public ScheduleDTO save(ScheduleDTO scheduleDTO) {
        LOG.debug("Request to save Schedule : {}", scheduleDTO);
        Schedule schedule = scheduleMapper.toEntity(scheduleDTO);
        schedule = scheduleRepository.save(schedule);
        scheduleSearchRepository.index(schedule);
        return scheduleMapper.toDto(schedule);
    }

    @Override
    public ScheduleDTO update(ScheduleDTO scheduleDTO) {
        LOG.debug("Request to update Schedule : {}", scheduleDTO);
        Schedule schedule = scheduleMapper.toEntity(scheduleDTO);
        schedule = scheduleRepository.save(schedule);
        scheduleSearchRepository.index(schedule);
        return scheduleMapper.toDto(schedule);
    }

    @Override
    public Optional<ScheduleDTO> partialUpdate(ScheduleDTO scheduleDTO) {
        LOG.debug("Request to partially update Schedule : {}", scheduleDTO);

        return scheduleRepository
            .findById(scheduleDTO.getId())
            .map(existingSchedule -> {
                scheduleMapper.partialUpdate(existingSchedule, scheduleDTO);

                return existingSchedule;
            })
            .map(scheduleRepository::save)
            .map(savedSchedule -> {
                scheduleSearchRepository.index(savedSchedule);
                return savedSchedule;
            })
            .map(scheduleMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ScheduleDTO> findOne(Long id) {
        LOG.debug("Request to get Schedule : {}", id);
        return scheduleRepository.findById(id).map(scheduleMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Schedule : {}", id);
        scheduleRepository.deleteById(id);
        scheduleSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleDTO> search(String query) {
        LOG.debug("Request to search Schedules for query {}", query);
        try {
            return StreamSupport.stream(scheduleSearchRepository.search(query).spliterator(), false).map(scheduleMapper::toDto).toList();
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
