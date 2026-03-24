import React, { useState, useEffect } from 'react';
import ScheduleTable from './ScheduleTable';
import ScheduleForm from './ScheduleForm';
import ConfirmDialog from './ConfirmDialog';
import { useSchedules } from '../hooks/useSchedules';
import { Schedule } from '../types';
import { Plus, AlertCircle } from 'lucide-react';
import '../styles/Dashboard.css';

const Dashboard: React.FC = () => {
  const { schedules, tasks, isLoading, error, createSchedule, updateSchedule, deleteSchedule, toggleSchedule } = useSchedules();
  const [showForm, setShowForm] = useState(false);
  const [editingSchedule, setEditingSchedule] = useState<Schedule | undefined>();
  const [successMessage, setSuccessMessage] = useState<string>('');
  const [confirmDelete, setConfirmDelete] = useState<{ isOpen: boolean; scheduleId: number | null }>({ isOpen: false, scheduleId: null });

  const handleCreate = () => {
    setEditingSchedule(undefined);
    setShowForm(true);
  };

  const handleEdit = (schedule: Schedule) => {
    setEditingSchedule(schedule);
    setShowForm(true);
  };

  const handleFormClose = () => {
    setShowForm(false);
    setEditingSchedule(undefined);
  };

  const handleFormSubmit = async (schedule: Schedule) => {
    const result = schedule.id
      ? await updateSchedule(schedule.id, schedule)
      : await createSchedule(schedule);

    if (result.success) {
      setSuccessMessage(result.message);
      handleFormClose();
    }
  };

  const handleDelete = async (id: number) => {
    setConfirmDelete({ isOpen: true, scheduleId: id });
  };

  const confirmDeleteAction = async () => {
    if (confirmDelete.scheduleId) {
      const result = await deleteSchedule(confirmDelete.scheduleId);
      if (result.success) {
        setSuccessMessage(result.message);
      }
    }
    setConfirmDelete({ isOpen: false, scheduleId: null });
  };

  const handleToggle = async (id: number) => {
    const result = await toggleSchedule(id);
    if (result.success) {
      setSuccessMessage(result.message);
    }
  };

  useEffect(() => {
    if (successMessage) {
      const timer = setTimeout(() => setSuccessMessage(''), 3000);
      return () => clearTimeout(timer);
    }
  }, [successMessage]);

  return (
    <div className="dashboard">
      <div className="dashboard-header">
        <h1>Scheduling System</h1>
        <p>Manage your scheduled tasks</p>
      </div>

      <div className="dashboard-content">
        {error && (
          <div className="alert alert-error">
            <AlertCircle size={20} />
            <span>{error}</span>
          </div>
        )}

        {successMessage && (
          <div className="alert alert-success">
            <span>{successMessage}</span>
          </div>
        )}

        <div className="dashboard-toolbar">
          <button className="btn btn-primary btn-large" onClick={handleCreate}>
            <Plus size={20} />
            Create Schedule
          </button>
        </div>

        <ScheduleTable
          schedules={schedules}
          isLoading={isLoading}
          onEdit={handleEdit}
          onDelete={handleDelete}
          onToggle={handleToggle}
        />
      </div>

      {showForm && (
        <ScheduleForm
          tasks={tasks}
          schedule={editingSchedule}
          onSubmit={handleFormSubmit}
          onCancel={handleFormClose}
        />
      )}

      <ConfirmDialog
        isOpen={confirmDelete.isOpen}
        title="Delete Schedule"
        message="Are you sure you want to delete this schedule? This action cannot be undone."
        onConfirm={confirmDeleteAction}
        onCancel={() => setConfirmDelete({ isOpen: false, scheduleId: null })}
      />
    </div>
  );
};

export default Dashboard;
