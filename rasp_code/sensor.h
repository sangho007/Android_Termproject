#ifndef SENSOR_H_
#define SENSOR_H_

#include "json.h"

void sensors_init();
sensors operate_sensors(sensors sensor_state, sensors new_state);

#endif  // SENSOR_H_
