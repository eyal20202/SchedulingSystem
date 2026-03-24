-- Run this if schedule_executions table doesn't exist

CREATE TABLE IF NOT EXISTS schedule_executions (
  id BIGSERIAL PRIMARY KEY,
  schedule_id BIGINT NOT NULL,
  schedule_name VARCHAR(255),
  task_id VARCHAR(100),
  execution_time TIMESTAMP NOT NULL,
  status VARCHAR(50) NOT NULL,
  duration_ms BIGINT,
  error_message VARCHAR(2000),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_executions_schedule_id ON schedule_executions(schedule_id);
CREATE INDEX IF NOT EXISTS idx_executions_status ON schedule_executions(status);
CREATE INDEX IF NOT EXISTS idx_executions_time ON schedule_executions(execution_time DESC);

-- Verify
SELECT COUNT(*) FROM schedule_executions;
