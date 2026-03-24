import { Schedule, ScheduleFrequencyType, ParameterType, Weekday, WEEKDAY_OPTIONS } from '../types';

const CRON_PARTS_COUNT = 6;

export const formatDateTime = (datetime: string | undefined): string => {
  if (!datetime) return '-';

  const parsedDate = new Date(datetime);
  if (Number.isNaN(parsedDate.getTime())) {
    return datetime;
  }

  return parsedDate.toLocaleString();
};

export const getFrequencyLabel = (frequency: ScheduleFrequencyType): string => {
  const labels: Record<ScheduleFrequencyType, string> = {
    [ScheduleFrequencyType.ONE_TIME]: 'One Time',
    [ScheduleFrequencyType.RECURRING_MINUTES]: 'Every X Minutes',
    [ScheduleFrequencyType.RECURRING_HOURS]: 'Every X Hours',
    [ScheduleFrequencyType.RECURRING_DAYS]: 'Every X Days',
    [ScheduleFrequencyType.WEEKLY]: 'Weekly',
    [ScheduleFrequencyType.CRON_EXPRESSION]: 'Cron Expression',
  };

  return labels[frequency] || frequency;
};

export const parseWeekDays = (weekDays?: string): Weekday[] => {
  if (!weekDays?.trim()) {
    return [];
  }

  return weekDays
    .split(',')
    .map((day) => day.trim().toUpperCase())
    .filter((day): day is Weekday => WEEKDAY_OPTIONS.includes(day as Weekday));
};

export const formatWeekDaysLabel = (weekDays?: string): string => {
  const parsedDays = parseWeekDays(weekDays);

  if (parsedDays.length === 0) {
    return 'No days selected';
  }

  return parsedDays
    .map((day) => `${day.charAt(0)}${day.slice(1).toLowerCase()}`)
    .join(', ');
};

export const normalizeDateTimeLocalValue = (value?: string): string => {
  if (!value) {
    return '';
  }

  const parsedDate = new Date(value);
  if (Number.isNaN(parsedDate.getTime())) {
    return value;
  }

  const localDate = new Date(parsedDate.getTime() - parsedDate.getTimezoneOffset() * 60000);
  return localDate.toISOString().slice(0, 16);
};

export const isValidCronExpression = (expression?: string): boolean => {
  if (!expression?.trim()) {
    return false;
  }

  const parts = expression.trim().split(/\s+/);
  return parts.length === CRON_PARTS_COUNT;
};

const validateParameterValue = (parameterName: string, value: string, type: ParameterType, required: boolean): string[] => {
  const errors: string[] = [];
  const trimmedValue = value?.trim() ?? '';

  if (required && !trimmedValue) {
    errors.push(`Parameter "${parameterName}" is required`);
    return errors;
  }

  if (!trimmedValue) {
    return errors;
  }

  switch (type) {
    case ParameterType.NUMBER:
      if (Number.isNaN(Number(trimmedValue))) {
        errors.push(`Parameter "${parameterName}" must be a valid number`);
      }
      break;
    case ParameterType.BOOLEAN:
      if (!['true', 'false'].includes(trimmedValue.toLowerCase())) {
        errors.push(`Parameter "${parameterName}" must be true or false`);
      }
      break;
    case ParameterType.DATE:
      if (Number.isNaN(new Date(trimmedValue).getTime())) {
        errors.push(`Parameter "${parameterName}" must be a valid date and time`);
      }
      break;
    default:
      break;
  }

  return errors;
};

export const validateSchedule = (schedule: Schedule): string[] => {
  const errors: string[] = [];

  if (!schedule.name?.trim()) {
    errors.push('Schedule name is required');
  }

  if (!schedule.taskId) {
    errors.push('Task is required');
  }

  if (!schedule.frequencyType) {
    errors.push('Frequency type is required');
  }

  if (schedule.frequencyType === ScheduleFrequencyType.ONE_TIME) {
    if (!schedule.oneTimeExecutionTime) {
      errors.push('Execution date and time are required for one-time schedules');
    } else if (Number.isNaN(new Date(schedule.oneTimeExecutionTime).getTime())) {
      errors.push('Execution date and time must be valid');
    }
  }

  if (
    [
      ScheduleFrequencyType.RECURRING_MINUTES,
      ScheduleFrequencyType.RECURRING_HOURS,
      ScheduleFrequencyType.RECURRING_DAYS,
    ].includes(schedule.frequencyType)
  ) {
    if (!schedule.frequencyValue || Number(schedule.frequencyValue) <= 0) {
      errors.push('Frequency value must be greater than 0');
    }
  }

  if (schedule.frequencyType === ScheduleFrequencyType.CRON_EXPRESSION) {
    if (!schedule.cronExpression?.trim()) {
      errors.push('Cron expression is required');
    } else if (!isValidCronExpression(schedule.cronExpression)) {
      errors.push('Cron expression must contain 6 parts: second minute hour day month dayOfWeek');
    }
  }

  if (schedule.frequencyType === ScheduleFrequencyType.WEEKLY) {
    const selectedDays = parseWeekDays(schedule.weekDays);

    if (selectedDays.length === 0) {
      errors.push('Select at least one week day for weekly schedules');
    }
  }

  schedule.parameters.forEach((parameter) => {
    errors.push(
      ...validateParameterValue(
        parameter.parameterName,
        parameter.parameterValue,
        parameter.parameterType,
        parameter.required
      )
    );
  });

  return errors;
};