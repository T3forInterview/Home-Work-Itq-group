
### Проверка индексов
1. Сначала необходимо создать несколько документов
2. Выполнить данный запрос                                        
   EXPLAIN ANALYZE
   SELECT d.id, d.uniq_number, d.author, d.name, d.status, d.initiator, d.created_date, d.update_date
   FROM documents d
   WHERE d.status = ''
   AND d.author LIKE '%%'
   AND d.created_date >= CURRENT_DATE - INTERVAL '7 days'
   AND d.created_date <= CURRENT_DATE

Проверить, что есть наличие записи Index Scan
