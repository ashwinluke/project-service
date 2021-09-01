CREATE TABLE `project_record` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(255) NOT NULL,
  `description` LONGTEXT NULL,
  `type` VARCHAR(2) NOT NULL COMMENT '_t1_ is TYPE-1, _t2_ is TYPE-3, _t3_ is TYPE-3',
  `status` VARCHAR(2) NOT NULL COMMENT '_df_ is DRAFT, _pb_ is PUBLISHED',
  `project_id` INT NOT NULL,
  PRIMARY KEY (`id`));
CREATE TABLE `section_record` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `title` VARCHAR(255) NOT NULL,
    `description` LONGTEXT NULL,
    `project_record_id` INT NOT NULL,
    PRIMARY KEY (`id`));
CREATE INDEX `fk_section_rd_2_project__rd_id_idx` ON `section_record`(project_record_id);
CREATE INDEX `fk_project_rd_2_project_id_idx` ON `project_record`(project_id);
ALTER TABLE `section_record`
    ADD CONSTRAINT `fk_section_rd_2_project_rd_id`
    FOREIGN KEY (`project_record_id`)
    REFERENCES `project_record` (`id`);
ALTER TABLE `project_record`
    ADD CONSTRAINT `fk_project_rd_2_project_id`
    FOREIGN KEY (`project_id`)
    REFERENCES `project` (`id`);