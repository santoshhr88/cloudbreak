{%- from 'filecollector/settings.sls' import filecollector with context %}

{% set extra_params="" %}
{% if filecollector.startTime %}
  {% set startTimeStr = filecollector.startTime|string %}
  {% set extra_params = extra_params + " --start-time " + startTimeStr %}
{% endif %}
{% if filecollector.endTime %}
  {% set endTimeStr = filecollector.endTime|string %}
  {% set extra_params = extra_params + " --end-time " + endTimeStr %}
{% endif %}
{% if filecollector.labelFilter %}
  {% set labels_str = "" %}
  {% for label in filecollector.labelFilter.items() %}
    {% set labels_str = label_str + " --label " + label %}
  {% endfor %}
  {% if labels_str %}
    {% set extra_params = extra_params + labels_str %}
  {% endif %}
{% endif %}

filecollector_collect_start:
  cmd.run:
{% if filecollector.destination in ["CLOUD_STORAGE", "LOCAL", "SUPPORT"] %}
    - name: "cdp-telemetry filecollector collect --config /opt/cdp-telemetry/conf/filecollector-collect.yaml {{ extra_params }}"
{% elif filecollector.destination == "ENG" %}
    - name: "cdp-telemetry filecollector collect --config /opt/cdp-telemetry/conf/filecollector-eng.yaml {{ extra_params }}"
{% else %}
    - name: 'echo Not supported destination: {{ filecollector.destination }}'
{% endif %}
    - env:
        - LC_ALL: "en_US.utf8"