CREATE TABLE busi_seat_group_mapping
(
   id serial primary key,
  busi_id          INTEGER,
  group_id         INTEGER,
  create_time      TIMESTAMP(6) WITH TIME ZONE DEFAULT now() NOT NULL,
  last_update_time TIMESTAMP(6) WITH TIME ZONE DEFAULT now() NOT NULL
);

CREATE INDEX group_id_bgm_idx
  ON busi_seat_group_mapping (group_id);

COMMENT ON COLUMN busi_seat_group_mapping.busi_id IS '业务线';

CREATE TABLE busi_seat_mapping
( id serial primary key,
  busi_id          INTEGER,
  seat_id          INTEGER,
  create_time      TIMESTAMP(6) WITH TIME ZONE DEFAULT now() NOT NULL,
  last_update_time TIMESTAMP(6) WITH TIME ZONE DEFAULT now() NOT NULL,
  status           INTEGER DEFAULT 1
);

CREATE INDEX seat_id_bsem_idx
  ON busi_seat_mapping (seat_id);

CREATE TABLE busi_supplier_mapping
(
   id serial primary key,
  supplier_id       INTEGER,
  busi_id           INTEGER,
  busi_supplier_id  VARCHAR(50),
  create_time       TIMESTAMP(6) WITH TIME ZONE DEFAULT now() NOT NULL,
  last_update_time  TIMESTAMP(6) WITH TIME ZONE DEFAULT now() NOT NULL,
  bsuid_and_type    VARCHAR(55),
  supplier_operator VARCHAR(50),
  operator_webname  VARCHAR(50),
  status            INTEGER DEFAULT 1
);

CREATE UNIQUE INDEX idx_tb_supplier_mapping_unq
  ON busi_supplier_mapping (busi_supplier_id, busi_id);

CREATE INDEX busi_supplier_id_bsm_idx
  ON busi_supplier_mapping (busi_supplier_id);
  
CREATE TABLE business
(
   id serial primary key,
  name             VARCHAR(30)
    CONSTRAINT business_name_key
    UNIQUE,
  create_time      TIMESTAMP(6) WITH TIME ZONE DEFAULT now() NOT NULL,
  last_update_time TIMESTAMP(6) WITH TIME ZONE DEFAULT now() NOT NULL,
  english_name     VARCHAR(25)
);

CREATE TABLE consult_msg_history
(
   id serial primary key,
  m_from      VARCHAR(255) DEFAULT NULL :: CHARACTER VARYING,
  m_to        VARCHAR(255) DEFAULT NULL :: CHARACTER VARYING,
  m_body      TEXT,
  create_time TIMESTAMP(6) WITH TIME ZONE DEFAULT now() NOT NULL,
  read_flag   SMALLINT     DEFAULT 0,
  msg_id      TEXT,
  from_host   TEXT         DEFAULT 'ejabhost2' :: TEXT,
  to_host     TEXT         DEFAULT 'ejabhost2' :: TEXT,
  realfrom    VARCHAR(255) DEFAULT NULL :: CHARACTER VARYING,
  realto      VARCHAR(255) DEFAULT NULL :: CHARACTER VARYING,
  msg_type    VARCHAR(255) DEFAULT 'consult' :: CHARACTER VARYING,
  status      SMALLINT     DEFAULT 0,
  qchat_id    TEXT DEFAULT (4) :: TEXT                  NOT NULL,
  chat_id     TEXT,
  update_time TIMESTAMP(6) WITH TIME ZONE DEFAULT now() NOT NULL
);

CREATE UNIQUE INDEX consult_msg_history_msg_id_idx
  ON consult_msg_history (msg_id);

CREATE TABLE consult_tag
(
   id serial primary key,
  title            VARCHAR(150) DEFAULT '' :: CHARACTER VARYING              NOT NULL,
  content          VARCHAR(250) DEFAULT '' :: CHARACTER VARYING              NOT NULL,
  supplier_id      INTEGER DEFAULT 0                                         NOT NULL,
  busi_supplier_id VARCHAR(50) DEFAULT '' :: CHARACTER VARYING               NOT NULL,
  pid              VARCHAR(50) DEFAULT '' :: CHARACTER VARYING               NOT NULL,
  status           INTEGER DEFAULT 1                                         NOT NULL,
  consult_type     INTEGER DEFAULT 0                                         NOT NULL,
  create_time      TIMESTAMP WITH TIME ZONE DEFAULT now()                    NOT NULL,
  update_time      TIMESTAMP WITH TIME ZONE DEFAULT now()                    NOT NULL,
  busi_id          INTEGER DEFAULT 0
);

COMMENT ON COLUMN consult_tag.status IS '0:无效,1:有效';

COMMENT ON COLUMN consult_tag.consult_type IS '0:未知,1:url,2:scheme,3:serverApi 后端接口链接接';

CREATE TABLE group_product_mapping
(
   id serial primary key,
  group_id         INTEGER,
  pid              VARCHAR(50),
  create_time      TIMESTAMP(6) WITH TIME ZONE DEFAULT now() NOT NULL,
  last_update_time TIMESTAMP(6) WITH TIME ZONE DEFAULT now() NOT NULL
);


CREATE TABLE hotline_supplier_mapping
(
   id serial primary key,
  hotline     VARCHAR(150) DEFAULT '' :: CHARACTER VARYING                       NOT NULL,
  supplier_id INTEGER DEFAULT 0                                                  NOT NULL,
  status      INTEGER DEFAULT 1                                                  NOT NULL,
  create_time TIMESTAMP WITH TIME ZONE DEFAULT now()                             NOT NULL,
  update_time TIMESTAMP WITH TIME ZONE DEFAULT now()                             NOT NULL
);

CREATE INDEX idx_tb_hotline_mapping_hotline_supplierid
  ON hotline_supplier_mapping (hotline)
  WHERE (status = 1);

CREATE INDEX idx_tb_hotline_mapping_supplierid
  ON hotline_supplier_mapping (supplier_id)
  WHERE (status = 1);

COMMENT ON COLUMN hotline_supplier_mapping.hotline IS '存qtalk热线账号 或 shop_name';

COMMENT ON COLUMN hotline_supplier_mapping.status IS '0:无效，1:有效';


CREATE TABLE log_operation
(
   id serial primary key,
  operate_type VARCHAR(25),
  item_type    VARCHAR(25),
  item_id      INTEGER,
  item_str     VARCHAR(125),
  operator     VARCHAR(25),
  operate_time TIMESTAMP WITH TIME ZONE,
  content      TEXT
);


CREATE TABLE page_template
(
   id serial primary key,
  name             VARCHAR(50),
  page_css         TEXT,
  page_html        TEXT,
  busi_id          INTEGER,
  create_time      TIMESTAMP(6) WITH TIME ZONE DEFAULT now() NOT NULL,
  last_update_time TIMESTAMP(6) WITH TIME ZONE DEFAULT now() NOT NULL
);


CREATE SEQUENCE queue_mapping_id_seq;
-- auto-generated definition
CREATE TABLE queue_mapping
(
  id               INTEGER DEFAULT nextval('queue_mapping_id_seq' :: REGCLASS)                                      NOT NULL,
  customer_name    VARCHAR(150) DEFAULT '' :: CHARACTER VARYING                                                     NOT NULL,
  shop_id          INTEGER DEFAULT 0                                                                                NOT NULL,
  product_id       VARCHAR(150) DEFAULT '*' :: CHARACTER VARYING                                                    NOT NULL,
  seat_id          INTEGER DEFAULT 0                                                                                NOT NULL,
  status           INTEGER DEFAULT 1                                                                                NOT NULL,
  request_count    INTEGER DEFAULT 1                                                                                NOT NULL,
  distributed_time TIMESTAMP WITH TIME ZONE DEFAULT to_timestamp(
      (0) :: DOUBLE PRECISION)                                                                                      NOT NULL,
  inqueue_time     TIMESTAMP WITH TIME ZONE DEFAULT now()                                                           NOT NULL,
  last_ack_time    TIMESTAMP WITH TIME ZONE DEFAULT now()                                                           NOT NULL,
  session_id       VARCHAR(50) DEFAULT replace(
      upper((uuid_in((md5(((random()) :: TEXT || (now()) :: TEXT))) :: CSTRING)) :: TEXT), '-' :: TEXT,
      '' :: TEXT)                                                                                                   NOT NULL
    CONSTRAINT queue_mapping_pkey
    PRIMARY KEY,
  seat_name        VARCHAR(150) DEFAULT '' :: CHARACTER VARYING                                                     NOT NULL,
  group_id         INTEGER DEFAULT 0
);

CREATE UNIQUE INDEX idx_tb_queue_mapping_userid_shopid_unq
  ON queue_mapping (customer_name, shop_id);

CREATE INDEX idx_tb_queue_mapping_last_ack_time_shop_id
  ON queue_mapping (last_ack_time, shop_id);

COMMENT ON COLUMN queue_mapping.status IS '1,lineup; 2,in service; 3,客人最后一句; 4,坐席最后一句; 5,坐席已释放';

COMMENT ON COLUMN queue_mapping.group_id IS '客服组id';


CREATE SEQUENCE queue_saved_message_id_seq;
CREATE TABLE queue_saved_message
(
  id            INTEGER DEFAULT nextval(
      'queue_saved_message_id_seq' :: REGCLASS)                                                                     NOT NULL,
  message_id    VARCHAR(50) DEFAULT replace(
      upper((uuid_in((md5(((random()) :: TEXT || (now()) :: TEXT))) :: CSTRING)) :: TEXT), '-' :: TEXT,
      '' :: TEXT)                                                                                                   NOT NULL
    CONSTRAINT queue_saved_message_pkey
    PRIMARY KEY,
  customer_name VARCHAR(150) DEFAULT '' :: CHARACTER VARYING                                                        NOT NULL,
  shop_id       INTEGER DEFAULT 0                                                                                   NOT NULL,
  message       JSONB DEFAULT '{}' :: JSONB                                                                         NOT NULL,
  op_time       TIMESTAMP WITH TIME ZONE DEFAULT now()                                                              NOT NULL
);

CREATE UNIQUE INDEX idx_queue_saved_message_id_unq
  ON queue_saved_message (id);

CREATE INDEX idx_tb_queue_saved_message
  ON queue_saved_message (customer_name, shop_id);

-- auto-generated definition
CREATE TABLE quickreply_content
( id serial primary key,
  username    VARCHAR(255) NOT NULL,
  host        VARCHAR(255) NOT NULL,
  groupid     BIGINT                   DEFAULT 1,
  content     VARCHAR(255) NOT NULL,
  contentseq  BIGINT                   DEFAULT 1,
  version     BIGINT                   DEFAULT 1,
  isdel       INTEGER                  DEFAULT 0,
  cgid        TEXT,
  ccid        TEXT,
  create_time TIMESTAMP WITH TIME ZONE DEFAULT now(),
  update_time TIMESTAMP WITH TIME ZONE DEFAULT now(),
  ext         TEXT
);

CREATE UNIQUE INDEX quickreply_content_groupid_content_contentseq_idx
  ON quickreply_content (groupid, content);

CREATE INDEX quickreply_content_groupid_version_isdel_idx
  ON quickreply_content (groupid, version, isdel);

CREATE INDEX quickreply_content_groupid_version_idx
  ON quickreply_content (groupid, version);

CREATE INDEX quickreply_content_groupid_idx
  ON quickreply_content (groupid);

CREATE INDEX quickreply_content_version_idx
  ON quickreply_content (version);

CREATE INDEX quickreply_content_isdel_idx
  ON quickreply_content (isdel);

CREATE INDEX quickreply_content_update_time_idx
  ON quickreply_content (update_time);

-- auto-generated definition
CREATE TABLE quickreply_group
(
   id serial primary key,
  username    VARCHAR(255) NOT NULL,
  host        VARCHAR(255) NOT NULL,
  groupname   VARCHAR(255) NOT NULL,
  groupseq    BIGINT                   DEFAULT 1,
  version     BIGINT                   DEFAULT 1,
  isdel       INTEGER                  DEFAULT 0,
  cgid        TEXT,
  create_time TIMESTAMP WITH TIME ZONE DEFAULT now(),
  update_time TIMESTAMP WITH TIME ZONE DEFAULT now(),
  ext         TEXT
);

CREATE UNIQUE INDEX quickreply_group_username_host_groupname_groupseq_idx
  ON quickreply_group (username, host, groupname);

CREATE INDEX quickreply_group_username_host_groupname_isdel_idx
  ON quickreply_group (username, host, groupname, isdel);

CREATE INDEX quickreply_group_username_host_groupname_idx
  ON quickreply_group (username, host, groupname);

CREATE INDEX quickreply_group_username_host_version_isdel_idx
  ON quickreply_group (username, host, version, isdel);

CREATE INDEX quickreply_group_username_host_version_idx
  ON quickreply_group (username, host, version);

CREATE INDEX quickreply_group_username_host_idx
  ON quickreply_group (username, host);

CREATE INDEX quickreply_group_username_idx
  ON quickreply_group (username);

CREATE INDEX quickreply_group_host_idx
  ON quickreply_group (host);

CREATE INDEX quickreply_group_groupname_idx
  ON quickreply_group (groupname);

CREATE INDEX quickreply_group_isdel_idx
  ON quickreply_group (isdel);

CREATE INDEX quickreply_group_update_time_idx
  ON quickreply_group (update_time);

CREATE TABLE robot_info
(
   id serial primary key,
  robot_id    VARCHAR(25)       NOT NULL,
  busi_id     INTEGER           NOT NULL,
  robot_name  VARCHAR(64),
  create_time TIMESTAMP WITH TIME ZONE DEFAULT now(),
  update_time TIMESTAMP WITH TIME ZONE DEFAULT now(),
  operator    VARCHAR(25),
  status      INTEGER DEFAULT 1 NOT NULL,
  imageurl    TEXT
);

CREATE TABLE robot_supplier_mapping
(
   id serial primary key,
  robot_id    VARCHAR(25) DEFAULT '' :: CHARACTER VARYING NOT NULL,
  supplier_id INTEGER,
  strategy    INTEGER,
  welcome     TEXT DEFAULT '' :: TEXT                     NOT NULL,
  create_time TIMESTAMP(6) WITH TIME ZONE DEFAULT now()   NOT NULL
);

CREATE UNIQUE INDEX uni_robot_supplier
  ON robot_supplier_mapping (robot_id, supplier_id);

-- auto-generated definition
CREATE TABLE seat
(
   id serial primary key,
  qunar_name       VARCHAR(40),
  web_name         VARCHAR(40),
  nickname         VARCHAR(50),
  face_link        VARCHAR(150),
  supplier_id      INTEGER,
  priority         DOUBLE PRECISION,
  create_time      TIMESTAMP(6) WITH TIME ZONE DEFAULT now() NOT NULL,
  last_update_time TIMESTAMP(6) WITH TIME ZONE DEFAULT now() NOT NULL,
  old_supplier_id  INTEGER,
  old_id           INTEGER,
  service_status   SMALLINT    DEFAULT 0,
  status           SMALLINT    DEFAULT 1,
  max_user         SMALLINT    DEFAULT 10,
  bind_wx          SMALLINT    DEFAULT '0' :: SMALLINT,
  host             VARCHAR(20) DEFAULT 'qtalk.openresty.org' :: CHARACTER VARYING
);

CREATE INDEX idx_seat_shopid_status_host
  ON seat (id, supplier_id, host)
  WHERE (status = 1);

CREATE INDEX seat_supplier_id_priority_idx
  ON seat (id)
  WHERE (status = 1);

CREATE INDEX idx_seat_name_host
  ON seat (qunar_name, host)
  WHERE (status = 1);

CREATE INDEX idx_seat_shopid_host_status
  ON seat (supplier_id, host)
  WHERE (status = 1);

CREATE INDEX supplier_id_seat_idx
  ON seat (supplier_id);

COMMENT ON COLUMN seat.service_status IS '服务状态，0: 标准模式 1：勿扰 4: 超人模式';

COMMENT ON COLUMN seat.status IS '客服的服务状态, 0:无效 1:有效';

COMMENT ON COLUMN seat.bind_wx IS '绑定微信，0：未绑定，1：已绑定';

COMMENT ON COLUMN seat.host IS '客服域';

CREATE TABLE seat_group
(
   id serial primary key,
  name             VARCHAR(40),
  strategy         INTEGER,
  supplier_id      INTEGER,
  create_time      TIMESTAMP(6) WITH TIME ZONE DEFAULT now() NOT NULL,
  last_update_time TIMESTAMP(6) WITH TIME ZONE DEFAULT now() NOT NULL,
  old_supplier_id  INTEGER,
  old_group_id     INTEGER,
  old_kefus        VARCHAR(4000),
  group_type       VARCHAR(125),
  group_priority   INTEGER  DEFAULT 1,
  default_value    SMALLINT DEFAULT 0
);

CREATE INDEX supplier_id_seat_group_idx
  ON seat_group (supplier_id);
CREATE TABLE seat_group_mapping
(
   id serial primary key,
  group_id         INTEGER,
  seat_id          INTEGER,
  create_time      TIMESTAMP(6) WITH TIME ZONE DEFAULT now() NOT NULL,
  last_update_time TIMESTAMP(6) WITH TIME ZONE DEFAULT now() NOT NULL
);

CREATE INDEX group_id_seat_id_sg_idx
  ON seat_group_mapping (group_id, seat_id);

CREATE TABLE seat_session
(
  id serial primary key,
  seat_id         INTEGER,
  last_start_time TIMESTAMP(6) WITH TIME ZONE DEFAULT now() NOT NULL
);

CREATE TABLE session
(
   id serial primary key,
  session_id    VARCHAR(255)                              NOT NULL,
  user_name     VARCHAR(255) DEFAULT NULL :: CHARACTER VARYING,
  seat_name     VARCHAR(255) DEFAULT NULL :: CHARACTER VARYING,
  shop_name     VARCHAR(255) DEFAULT NULL :: CHARACTER VARYING,
  product_id    TEXT,
  session_state SMALLINT     DEFAULT 0,
  isrobot_seat  SMALLINT     DEFAULT 0,
  created_time  TIMESTAMP(6) WITH TIME ZONE DEFAULT now() NOT NULL,
  update_time   TIMESTAMP(6) WITH TIME ZONE DEFAULT now() NOT NULL
);

CREATE SEQUENCE session_mapping_id_seq;

CREATE TABLE session_mapping
(
  id               INTEGER DEFAULT nextval('session_mapping_id_seq' :: REGCLASS)          NOT NULL,
  customer_name    VARCHAR(150) DEFAULT '' :: CHARACTER VARYING                           NOT NULL,
  shop_id          INTEGER DEFAULT 0                                                      NOT NULL,
  product_id       VARCHAR(150) DEFAULT '*' :: CHARACTER VARYING                          NOT NULL,
  session_id       VARCHAR(50)                                                            NOT NULL
    CONSTRAINT session_mapping_pkey
    PRIMARY KEY,
  seat_id          INTEGER DEFAULT 0                                                      NOT NULL,
  status           INTEGER DEFAULT 1                                                      NOT NULL,
  request_count    INTEGER DEFAULT 1                                                      NOT NULL,
  distributed_time TIMESTAMP WITH TIME ZONE DEFAULT to_timestamp((0) :: DOUBLE PRECISION) NOT NULL,
  inqueue_time     TIMESTAMP WITH TIME ZONE DEFAULT now()                                 NOT NULL,
  last_ack_time    TIMESTAMP WITH TIME ZONE DEFAULT now()                                 NOT NULL,
  seat_name        VARCHAR(150) DEFAULT '' :: CHARACTER VARYING                           NOT NULL,
  group_id         INTEGER DEFAULT 0
);

COMMENT ON COLUMN session_mapping.group_id IS '客服组id';

CREATE TABLE supplier
(
   id serial primary key,
  name                VARCHAR(100),
  create_time         TIMESTAMP(6) WITH TIME ZONE DEFAULT now() NOT NULL,
  last_update_time    TIMESTAMP(6) WITH TIME ZONE DEFAULT now() NOT NULL,
  old_id              INTEGER,
  logo_url            VARCHAR(255),
  welcomes            VARCHAR(512),
  status              INTEGER  DEFAULT 1,
  ext_flag            SMALLINT DEFAULT 0,
  assign_strategy     SMALLINT DEFAULT '0' :: SMALLINT,
  no_service_welcomes VARCHAR  DEFAULT '小驼的工作时间为周一至周五，早上9:00-19:00，您可以继续咨询，小驼上线看到后会第一时间回复~' :: CHARACTER VARYING
);

COMMENT ON COLUMN supplier.status IS '商铺的状态：1为在线，0为下线';

COMMENT ON COLUMN supplier.ext_flag IS '否启用排队：1为启用排队，0为未启用排队';

COMMENT ON COLUMN supplier.assign_strategy IS '分配策略，0：轮询，1：最闲优先，2：随机分配';

COMMENT ON COLUMN supplier.no_service_welcomes IS '无人工客服响应下的欢迎语';

CREATE TABLE sys_user
(
   id serial primary key,
  qunar_name       VARCHAR(40),
  supplier_id      INTEGER,
  create_time      TIMESTAMP(6) WITH TIME ZONE DEFAULT now() NOT NULL,
  last_update_time TIMESTAMP(6) WITH TIME ZONE DEFAULT now() NOT NULL
);

CREATE INDEX qunar_name_sys_user_idx
  ON sys_user (qunar_name);

  