CREATE TABLE projects (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    cpu_limit_cores NUMERIC(2,1) NOT NULL CHECK (cpu_limit_cores >= 0.1),
    memory_limit_mb INTEGER NOT NULL CHECK (memory_limit_mb BETWEEN 128 AND 4096),
    max_concurrent_runs INTEGER NOT NULL CHECK (max_concurrent_runs BETWEEN 1 AND 5),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE pipelines (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL REFERENCES projects(id),
    name VARCHAR(60) NOT NULL,
    repo_url VARCHAR(255) NOT NULL,
    branch VARCHAR(100) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    UNIQUE (project_id, name)
);

CREATE TABLE steps (
    id BIGSERIAL PRIMARY KEY,
    pipeline_id BIGINT NOT NULL REFERENCES pipelines(id) ON DELETE CASCADE,
    order_index INTEGER NOT NULL CHECK (order_index >= 1),
    name VARCHAR(50) NOT NULL,
    image VARCHAR(255) NOT NULL,
    command TEXT NOT NULL,
    timeout_seconds INTEGER NOT NULL CHECK (timeout_seconds BETWEEN 10 AND 3600),
    UNIQUE (pipeline_id, order_index)
);

CREATE TABLE runs (
    id BIGSERIAL PRIMARY KEY,
    pipeline_id BIGINT NOT NULL REFERENCES pipelines(id) ON DELETE CASCADE,
    status VARCHAR(20) NOT NULL,
    triggered_by VARCHAR(100) NOT NULL,
    cancel_requested BOOLEAN NOT NULL DEFAULT FALSE,
    k8s_job_name VARCHAR(63),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    started_at TIMESTAMP WITHOUT TIME ZONE,
    finished_at TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE step_runs (
    id BIGSERIAL PRIMARY KEY,
    run_id BIGINT NOT NULL REFERENCES runs(id) ON DELETE CASCADE,
    order_index INTEGER NOT NULL,
    name VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL,
    exit_code INTEGER,
    log TEXT,
    started_at TIMESTAMP WITHOUT TIME ZONE,
    finished_at TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE allowed_images (
    id BIGSERIAL PRIMARY KEY,
    pattern VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(255)
);

CREATE USER "kubeci" WITH PASSWORD 'kubeci';
GRANT USAGE ON SCHEMA public TO "kubeci";
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO "kubeci";
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO "kubeci";