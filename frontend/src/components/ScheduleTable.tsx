import React from 'react';
import { Schedule } from '../types';
import { formatDateTime, getFrequencyLabel } from '../utils/helpers';
import { Trash2, Edit2, Power } from 'lucide-react';
import '../styles/ScheduleTable.css';

interface ScheduleTableProps {
  schedules: Schedule[];
  isLoading: boolean;
  onEdit: (schedule: Schedule) => void;
  onDelete: (id: number) => void;
  onToggle: (id: number) => void;
}

const ScheduleTable: React.FC<ScheduleTableProps> = ({
  schedules,
  isLoading,
  onEdit,
  onDelete,
  onToggle,
}) => {
  if (isLoading) {
    return <div className="loading">Loading schedules...</div>;
  }

  if (schedules.length === 0) {
    return <div className="empty-state">No schedules found. Create one to get started!</div>;
  }

  return (
    <div className="schedule-table-container">
      <table className="schedule-table">
        <thead>
          <tr>
            <th>Name</th>
            <th>Task</th>
            <th>Frequency</th>
            <th>Status</th>
            <th>Next Execution</th>
            <th>Last Execution</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {schedules.map((schedule) => (
            <tr key={schedule.id} className={!schedule.enabled ? 'disabled' : ''}>
              <td className="name-cell">{schedule.name}</td>
              <td>{schedule.taskId}</td>
              <td>{getFrequencyLabel(schedule.frequencyType)}</td>
              <td>
                <span className={`status-badge status-${schedule.status?.toLowerCase()}`}>
                  {schedule.status}
                </span>
              </td>
              <td>{formatDateTime(schedule.nextExecutionTime)}</td>
              <td>{formatDateTime(schedule.lastExecutionTime)}</td>
              <td className="actions-cell">
                <button
                  className="btn-icon btn-toggle"
                  onClick={() => onToggle(schedule.id!)}
                  title={schedule.enabled ? 'Disable' : 'Enable'}
                >
                  <Power size={16} />
                </button>
                <button
                  className="btn-icon btn-edit"
                  onClick={() => onEdit(schedule)}
                  title="Edit"
                >
                  <Edit2 size={16} />
                </button>
                <button
                  className="btn-icon btn-delete"
                  onClick={() => onDelete(schedule.id!)}
                  title="Delete"
                >
                  <Trash2 size={16} />
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default ScheduleTable;
