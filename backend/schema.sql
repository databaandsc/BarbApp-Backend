-- 1) ENUMs
DO $$ BEGIN
CREATE TYPE role_type AS ENUM ('CLIENT', 'BARBER', 'ADMIN');
EXCEPTION
  WHEN duplicate_object THEN NULL;
END $$;

DO $$ BEGIN
CREATE TYPE appointment_status AS ENUM ('PENDING', 'CONFIRMED', 'REJECTED', 'CANCELLED', 'NO_SHOW');
EXCEPTION
  WHEN duplicate_object THEN NULL;
END $$;

-- 2) ACCOUNTS (dominio) vinculado a auth.users
CREATE TABLE IF NOT EXISTS public.accounts (
                                               id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    auth_user_id uuid UNIQUE NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,

    name text NOT NULL,
    surname text,
    phone text,

    role role_type NOT NULL DEFAULT 'CLIENT',
    is_active boolean NOT NULL DEFAULT true,

    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
    );

-- 3) SERVICES
CREATE TABLE IF NOT EXISTS public.services (
                                               id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name text NOT NULL,
    description text,
    price numeric(10,2) NOT NULL CHECK (price >= 0),
    duration_minutes int NOT NULL CHECK (duration_minutes > 0),
    is_active boolean NOT NULL DEFAULT true,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
    );

-- 4) APPOINTMENTS
CREATE TABLE IF NOT EXISTS public.appointments (
                                                   id uuid PRIMARY KEY DEFAULT gen_random_uuid(),

    client_id uuid NOT NULL REFERENCES public.accounts(id),
    barber_id uuid NOT NULL REFERENCES public.accounts(id),

    start_at timestamptz NOT NULL,
    end_at timestamptz NOT NULL CHECK (end_at > start_at),

    status appointment_status NOT NULL DEFAULT 'PENDING',

    total_price numeric(10,2) NOT NULL CHECK (total_price >= 0),
    total_duration_minutes int NOT NULL CHECK (total_duration_minutes > 0),

    decided_at timestamptz NULL,
    decided_by uuid NULL REFERENCES public.accounts(id),

    client_notes text NULL,
    internal_notes text NULL,

    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
    );

-- 5) APPOINTMENT_SERVICES
CREATE TABLE IF NOT EXISTS public.appointment_services (
                                                           id uuid PRIMARY KEY DEFAULT gen_random_uuid(),

    appointment_id uuid NOT NULL REFERENCES public.appointments(id) ON DELETE CASCADE,
    service_id uuid NOT NULL REFERENCES public.services(id),

    quantity int NOT NULL DEFAULT 1 CHECK (quantity >= 1),

    price_snapshot numeric(10,2) NOT NULL CHECK (price_snapshot >= 0),
    duration_snapshot_minutes int NOT NULL CHECK (duration_snapshot_minutes > 0),

    UNIQUE (appointment_id, service_id)
    );

-- 6) BARBER_SCHEDULES
CREATE TABLE IF NOT EXISTS public.barber_schedules (
                                                       id uuid PRIMARY KEY DEFAULT gen_random_uuid(),

    barber_id uuid NOT NULL REFERENCES public.accounts(id) ON DELETE CASCADE,
    weekday int NOT NULL CHECK (weekday BETWEEN 0 AND 6),

    start_time time NOT NULL,
    end_time time NOT NULL CHECK (end_time > start_time),

    is_active boolean NOT NULL DEFAULT true,

    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
    );

-- 7) BARBER_TIME_OFF
CREATE TABLE IF NOT EXISTS public.barber_time_off (
                                                      id uuid PRIMARY KEY DEFAULT gen_random_uuid(),

    barber_id uuid NOT NULL REFERENCES public.accounts(id) ON DELETE CASCADE,

    start_at timestamptz NOT NULL,
    end_at timestamptz NOT NULL CHECK (end_at > start_at),

    reason text,

    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
    );