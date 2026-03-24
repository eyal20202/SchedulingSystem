import React, { useState, useEffect } from 'react';
import { Schedule, TaskDefinition, ScheduleFrequencyType, ParameterType, ScheduleParameter } from '../types';
import { validateSchedule } from '../utils/helpers';
import { X } from 'lucide-react';
import '../styles/ScheduleForm.css';

interface ScheduleFormProps {
  tasks: TaskDefinition[];
  schedule?: Schedule;
  onSubmit: (schedule: Schedule) => void;
  onCancel: () => void;
}

const ScheduleForm: React.FC<ScheduleFormProps> = ({ tasks, schedule, onSubmit, onCancel }) => {
  // Format datetime for datetime-local input
  const formatDateTimeLocal = (dateString: string | null | undefined): string => {
    if (!dateString) return '';
    try {
      const date = new Date(dateString);
      if (isNaN(date.getTime())) return '';
      // Format: YYYY-MM-DDTHH:mm
      return date.toISOString().slice(0, 16);
    } catch {
      return '';
    }
  };

  const [formData, setFormData] = useState<Schedule>({
    name: '',
    description: '',
    taskId: '',
    frequencyType: ScheduleFrequencyType.ONE_TIME,
    oneTimeExecutionTime: '',
    enabled: true,
    parameters: [],
  });

  const [selectedTask, setSelectedTask] = useState<TaskDefinition | null>(null);
  const [errors, setErrors] = useState<string[]>([]);
  const [parameterValues, setParameterValues] = useState<Record<string, string>>({});

  // Initialize form when schedule prop is provided or changes
  useEffect(() => {
    if (schedule) {
      console.log('Initializing form with schedule:', schedule);
      console.log('oneTimeExecutionTime:', schedule.oneTimeExecutionTime);
      const formattedDateTime = formatDateTimeLocal(schedule.oneTimeExecutionTime);
      console.log('Formatted dateTime:', formattedDateTime);
      
      setFormData({
        ...schedule,
        oneTimeExecutionTime: formattedDateTime,
      });
      
      // Initialize parameter values
      if (schedule.parameters) {
        const values: Record<string, string> = {};
        schedule.parameters.forEach((p) => {
          values[p.parameterName] = p.parameterValue;
        });
        setParameterValues(values);
      }
    } else {
      // Reset form for new schedule
      setFormData({
        name: '',
        description: '',
        taskId: '',
        frequencyType: ScheduleFrequencyType.ONE_TIME,
        oneTimeExecutionTime: '',
        enabled: true,
        parameters: [],
      });
      setParameterValues({});
    }
  }, [schedule]);

  useEffect(() => {
    // Update selected task when taskId changes
    const task = tasks.find((t) => t.id === formData.taskId);
    setSelectedTask(task || null);
  }, [formData.taskId, tasks]);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value, type } = e.target;
    const checked = (e.target as HTMLInputElement).checked;

    setFormData((prev) => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value,
    }));
  };

  const handleParameterChange = (paramName: string, value: string) => {
    setParameterValues((prev) => ({
      ...prev,
      [paramName]: value,
    }));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    // Build parameters array
    const parameters: ScheduleParameter[] = [];
    if (selectedTask) {
      selectedTask.parameters.forEach((paramDef) => {
        const value = parameterValues[paramDef.name] || paramDef.defaultValue || '';
        parameters.push({
          parameterName: paramDef.name,
          parameterValue: value,
          parameterType: paramDef.type,
          required: paramDef.required,
        });
      });
    }

    const scheduleToSubmit = { ...formData, parameters };

    // Validate
    const validationErrors = validateSchedule(scheduleToSubmit);
    if (validationErrors.length > 0) {
      setErrors(validationErrors);
      return;
    }

    setErrors([]);
    onSubmit(scheduleToSubmit);
  };

  return (
    <div className="schedule-form-overlay">
      <div className="schedule-form">
        <div className="form-header">
          <h2>{schedule ? 'Edit Schedule' : 'Create New Schedule'}</h2>
          <button className="btn-close" onClick={onCancel}>
            <X size={20} />
          </button>
        </div>

        {errors.length > 0 && (
          <div className="error-list">
            {errors.map((error, idx) => (
              <div key={idx} className="error-item">
                {error}
              </div>
            ))}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Schedule Name *</label>
            <input
              type="text"
              name="name"
              value={formData.name}
              onChange={handleInputChange}
              placeholder="e.g., Daily Log Backup"
              required
            />
          </div>

          <div className="form-group">
            <label>Description</label>
            <textarea
              name="description"
              value={formData.description || ''}
              onChange={handleInputChange}
              placeholder="Optional description"
              rows={2}
            />
          </div>

          <div className="form-group">
            <label>Task *</label>
            <select name="taskId" value={formData.taskId} onChange={handleInputChange} required>
              <option value="">Select a task</option>
              {tasks.map((task) => (
                <option key={task.id} value={task.id}>
                  {task.name} - {task.description}
                </option>
              ))}
            </select>
          </div>

          <div className="form-group">
            <label>Frequency Type *</label>
            <select name="frequencyType" value={formData.frequencyType} onChange={handleInputChange} required>
              <option value={ScheduleFrequencyType.ONE_TIME}>One Time</option>
              <option value={ScheduleFrequencyType.RECURRING_MINUTES}>Every X Minutes</option>
              <option value={ScheduleFrequencyType.RECURRING_HOURS}>Every X Hours</option>
              <option value={ScheduleFrequencyType.RECURRING_DAYS}>Every X Days</option>
              <option value={ScheduleFrequencyType.WEEKLY}>Weekly</option>
              <option value={ScheduleFrequencyType.CRON_EXPRESSION}>Cron Expression</option>
            </select>
          </div>

          {formData.frequencyType === ScheduleFrequencyType.ONE_TIME && (
            <div className="form-group">
              <label>Execution Date & Time *</label>
              <input
                type="datetime-local"
                name="oneTimeExecutionTime"
                value={formData.oneTimeExecutionTime || ''}
                onChange={handleInputChange}
                required
              />
            </div>
          )}

          {[
            ScheduleFrequencyType.RECURRING_MINUTES,
            ScheduleFrequencyType.RECURRING_HOURS,
            ScheduleFrequencyType.RECURRING_DAYS,
          ].includes(formData.frequencyType) && (
            <div className="form-group">
              <label>Frequency Value *</label>
              <input
                type="number"
                name="frequencyValue"
                value={formData.frequencyValue || ''}
                onChange={handleInputChange}
                min="1"
                placeholder="e.g., 5"
                required
              />
            </div>
          )}

          {formData.frequencyType === ScheduleFrequencyType.CRON_EXPRESSION && (
            <div className="form-group">
              <label>Cron Expression *</label>
              <input
                type="text"
                name="cronExpression"
                value={formData.cronExpression || ''}
                onChange={handleInputChange}
                placeholder="e.g., 0 0 12 * * ?"
                required
              />
              <small>Format: second minute hour day month dayOfWeek</small>
              <div className="cron-examples">
                <strong>Examples:</strong>
                <button type="button" className="cron-preset" onClick={() => setFormData(prev => ({ ...prev, cronExpression: '0 0 12 * * ?' }))}>Daily at noon</button>
                <button type="button" className="cron-preset" onClick={() => setFormData(prev => ({ ...prev, cronExpression: '0 0 9 * * MON-FRI' }))}>Weekdays at 9 AM</button>
                <button type="button" className="cron-preset" onClick={() => setFormData(prev => ({ ...prev, cronExpression: '0 0 0 1 * ?' }))}>First day of month</button>
              </div>
            </div>
          )}

          {formData.frequencyType === ScheduleFrequencyType.WEEKLY && (
            <div className="form-group">
              <label>Week Days *</label>
              <div className="weekdays-selector">
                {['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'].map((day) => (
                  <label key={day} className="weekday-checkbox">
                    <input
                      type="checkbox"
                      checked={(formData.weekDays || '').split(',').map(d => d.trim()).includes(day)}
                      onChange={(e) => {
                        const currentDays = (formData.weekDays || '').split(',').map(d => d.trim()).filter(d => d);
                        const newDays = e.target.checked
                          ? [...currentDays, day]
                          : currentDays.filter(d => d !== day);
                        setFormData(prev => ({ ...prev, weekDays: newDays.join(',') }));
                      }}
                    />
                    {day.charAt(0) + day.slice(1).toLowerCase()}
                  </label>
                ))}
              </div>
            </div>
          )}

          {selectedTask && selectedTask.parameters.length > 0 && (
            <fieldset className="parameters-section">
              <legend>Task Parameters</legend>
              {selectedTask.parameters.map((param) => (
                <div key={param.name} className="form-group">
                  <label>
                    {param.name}
                    {param.required && <span className="required">*</span>}
                  </label>
                  {param.type === ParameterType.STRING && (
                    <input
                      type="text"
                      value={parameterValues[param.name] || ''}
                      onChange={(e) => handleParameterChange(param.name, e.target.value)}
                      placeholder={param.description || ''}
                      required={param.required}
                    />
                  )}
                  {param.type === ParameterType.NUMBER && (
                    <input
                      type="number"
                      value={parameterValues[param.name] || ''}
                      onChange={(e) => handleParameterChange(param.name, e.target.value)}
                      placeholder={param.description || ''}
                      required={param.required}
                    />
                  )}
                  {param.type === ParameterType.BOOLEAN && (
                    <input
                      type="checkbox"
                      checked={parameterValues[param.name] === 'true'}
                      onChange={(e) => handleParameterChange(param.name, e.target.checked ? 'true' : 'false')}
                    />
                  )}
                  {param.type === ParameterType.DATE && (
                    <input
                      type="datetime-local"
                      value={parameterValues[param.name] || ''}
                      onChange={(e) => handleParameterChange(param.name, e.target.value)}
                      required={param.required}
                    />
                  )}
                  {param.description && <small>{param.description}</small>}
                </div>
              ))}
            </fieldset>
          )}

          <div className="form-group checkbox">
            <label>
              <input
                type="checkbox"
                name="enabled"
                checked={formData.enabled}
                onChange={handleInputChange}
              />
              Enabled
            </label>
          </div>

          <div className="form-actions">
            <button type="button" className="btn btn-secondary" onClick={onCancel}>
              Cancel
            </button>
            <button type="submit" className="btn btn-primary">
              {schedule ? 'Update' : 'Create'} Schedule
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default ScheduleForm;
