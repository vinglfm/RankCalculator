CREATE TABLE IF NOT EXISTS public.StrengthParameters (
	id SERIAL PRIMARY KEY,
	name varchar(20) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS public.StrengthInfo (
	userId varchar(20),
	measurementDate date NOT NULL,
	parameter smallint NOT NULL REFERENCES public.StrengthParameters (id),
	parameterValue smallint NOT NULL,
	PRIMARY KEY(userId, measurementDate, parameter)
	);
CREATE INDEX strengthInfo_userId_idx ON public.StrengthInfo(userId);

CREATE TABLE IF NOT EXISTS public.StrongmenRank (
	rankName varchar(10) NOT NULL,
	rankLow smallint NOT NULL,
	rankHigh smallint NOT NULL,
	CHECK (rankLow < rankHigh AND rankLow >= 0 AND rankHigh > 0)
)