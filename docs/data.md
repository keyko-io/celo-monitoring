# Monitoring Data

The Celo Monitoring tool generates data in Elastic Search. 
You can get a complete list of the existing indices sending the following request in Elastic:

```bash
curl --location -u $ELASTIC_USER:$ELASTIC_PASSWORD -X GET 'http://localhost:9200/_cat/indices?v=' \
--header 'Content-Type: application/json' \
--data-raw ''
```

Here you have a list of the existing metrics created: 

Metric | Type | Url Elastic | Url Schema | Url Dashboard
-----|------|-------------|------------|------------
Gold Balance | View | [Elastic Index](http://localhost:9200/w3m-reserve-getreservegoldbalance_elastic/_search?q=*) | [Schema](http://localhost:18081/subjects/w3m-reserve-getreservegoldbalance_elastic-value/versions/1) | [Explore in Kibana](http://localhost:5601/app/kibana#/dashboard/af5d21d0-586c-11ea-aa1c-5dd117a31dfd)
Exchanged | Event | [Elastic Index](http://localhost:9200/w3m-exchanged_elastic/_search?q=*) | [Schema](http://localhost:18081/w3m-exchanged_elastic-value/versions/1) | [Explore in Kibana](http://localhost:5601/app/kibana#/dashboard/af5d21d0-586c-11ea-aa1c-5dd117a31dfd)
Epoch Rewards Distributed to Voters| Event | [Elastic Index](http://localhost:9200/w3m-epochrewardsdistributedtovoters_elastic) | [Schema](http://localhost:18081/subjects/w3m-epochrewardsdistributedtovoters_elastic-value/versions/1) | [Explore in Kibana](http://localhost:5601/app/kibana#/dashboard/9fc3df10-57e6-11ea-9421-39e78e9111f6)
Epoch Rewards Multiplier| View | [Elastic Index](http://localhost:9200/w3m-epochrewards-getrewardsmultiplier_elastic/_search?q=*) | [Schema](http://localhost:18081/subjects/w3m-epochrewards-getrewardsmultiplier_elastic-value/versions/1) | [Explore in Kibana](http://localhost:5601/app/kibana#/dashboard/9fc3df10-57e6-11ea-9421-39e78e9111f6)
Gold Token Total Supply| View | [Elastic Index](http://localhost:9200/w3m-goldtoken-totalsupply_elastic/_search?q=*) | [Schema](http://localhost:18081/subjects/w3m-goldtoken-totalsupply_elastic-value/versions/1) | [Explore in Kibana](http://localhost:5601/app/kibana#/dashboard/533829c0-52fb-11ea-a9c2-f59efceba7db)
Epoch Rewards Get Target Gold Total Supply| View | [Elastic Index](http://localhost:9200/w3m-epochrewards-gettargetgoldtotalsupply_elastic/_search?q=*) | [Schema](http://localhost:18081/subjects/w3m-epochrewards-gettargetgoldtotalsupply_elastic-value/versions/1) | [Explore in Kibana](http://localhost:5601/app/kibana#/dashboard/533829c0-52fb-11ea-a9c2-f59efceba7db)
Validator Epoch Payment Distributed| Event | [Elastic Index](http://localhost:9200/w3m-validatorepochpaymentdistributed_elastic/_search?q=*) | [Schema](http://localhost:18081/subjects/w3m-validatorepochpaymentdistributed_elastic-value/versions/1) | [Explore in Kibana](http://localhost:5601/app/kibana#/dashboard/9fc3df10-57e6-11ea-9421-39e78e9111f6)
Target Voting Yield updated| Event| [Elastic Index](http://localhost:9200/w3m-targetvotingyieldupdated_elastic/_search?q=*) | [Schema](http://localhost:18081/subjects/w3m-targetvotingyieldupdated_elastic-value/versions/1) | [Explore in Kibana](http://localhost:5601/app/kibana#/dashboard/9fc3df10-57e6-11ea-9421-39e78e9111f6)
Buy And Sell Buckets | View | [Elastic Index](http://localhost:9200/w3m-exchange-getbuyandsellbuckets_elastic/_search?q=*) | [Schema](http://localhost:18081/subjects/w3m-exchange-getbuyandsellbuckets_elastic-value/versions/1) | [Explore in Kibana](http://localhost:5601/app/kibana#/dashboard/af5d21d0-586c-11ea-aa1c-5dd117a31dfd)
Median rate| View | [Elastic Index](http://localhost:9200/w3m-sortedoracles-medianrate_elastic/_search?q=*) | [Schema](http://localhost:18081/subjects/w3m-sortedoracles-medianrate_elastic-value/versions/1) | [Explore in Kibana](http://localhost:5601/app/kibana#/dashboard/9fc3df10-57e6-11ea-9421-39e78e9111f6)

In addition to this, the historic data of Blocks, Events and Views crawled by the agent can be found in the following pages:

Topic | Url Elastic | Url Kibana
------|-------------|------------
Blocks| [Elastic Index](http://localhost:9200/w3m-block-events/_search?q=*) | [Explore in Kibana](http://localhost:5601/goto/cd5f94695eeb020f7ec1f869bb7e228f)
Events| [Elastic Index](http://localhost:9200/w3m-contract-events/_search?q=*) | [Explore in Kibana](http://localhost:5601/goto/18f71e84d4c97102e1b8521282bb01e3)
Views (Public State)| [Elastic Index](http://localhost:9200/w3m-contract-views/_search?q=*)        | [Explore in Kibana](http://localhost:5601/goto/39eaaed4e58cde52cd2630fade6f2e95)

