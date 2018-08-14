import { Component, OnInit } from '@angular/core';
import { ModelService } from '../model.service';
import * as Highcharts from 'highcharts';
import * as Streamgraph from 'highcharts/modules/streamgraph';
import { SpeciesService } from '../species.service';

@Component({
    selector: 'app-home',
    templateUrl: './home.component.html',
    styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

    private averagePerGenerationChart;
    private speciesChart;

    constructor(public model: ModelService, public species: SpeciesService) { }

    ngOnInit() {
        Streamgraph(Highcharts);
        this.createAveragePerGenerationChart();
        this.createSpeciesChart();
        
        console.log(this.speciesChart);

        this.model.addModelListener((key, value) => {
            if (key == 'generationResults') {
                value = value.slice().reverse();
                //
                let series = this.averagePerGenerationChart.series;
                let lastPoint = series.length == 0 ? 0 : series[0].data.length;
                for (let i = lastPoint; i < value.length; i++) {
                    let generation = value[i];
                    series[0].addPoint(generation.best.average * 100);
                    series[1].addPoint(generation.average * 100);
                    series[2].addPoint(generation.worst.average * 100);
                }
                //
                series = this.speciesChart.series;
                // lastPoint = series.length == 0 ? 0 : series[0].data.length;
                /*
                for (let i = lastPoint; i < value.length; i++) {
                    let generation = value[i];
                    let seriesCount = {};
                    for (let individual of generation.individuals) {
                        let species = this.species.getSpecies(individual.chromossome);
                        seriesCount[species] = (seriesCount[species] || 0) + 1;
                    }



                    for (let j in seriesCount) {
                        if (series.findIndex((s) => s.name == j) < 0) {
                            let emptyArray = [];
                            for (let j = 0; j < i - 1; j++) {
                                emptyArray.push(0);
                            }
                            this.speciesChart.addSeries({name: j, data: emptyArray}, true, true);
                        }
                    }

                    for (let j in series) {
                        series[j].addPoint(seriesCount[series[j].name] || 0, true, true);
                    }
                }*/


            }
        });

        

    }

    createAveragePerGenerationChart() {
        let data = [{
            name: 'Melhor',
            color: '#218838',
            data: []
        }, {
            name: 'Média',
            color: '#868e96',
            data: []
        },
        {
            name: 'Pior',
            color: '#dc3545',
            data: []
        }];

        
        for (let generation of (<any>this.model).generationResults.slice().reverse()) {
            data[0].data.push(generation.best.average * 100);
            data[1].data.push(generation.average * 100);
            data[2].data.push(generation.worst.average * 100);
        }

        this.averagePerGenerationChart = Highcharts.chart('averagePerGeneration', {
            chart: {
                type: 'line',
                animation: Highcharts.svg,
                height: 300 
            },
            title: {
                text: ""
            },
            xAxis: {
                labels: {
                    overflow: 'justify'
                },
                tickInterval: 1
            },
            yAxis: {
                title: {
                    text: 'Média de Fitness (%)'
                },
                min: 0,
                max: 100,
                minorGridLineWidth: 1,
                gridLineWidth: 1,
                alternateGridColor: null,
            },
            credits: {
                enabled: false,
            },
            tooltip: {
                valueSuffix: '%'
            },
            legend: {
                enabled: false,
            },
            plotOptions: {
                spline: {
                    lineWidth: 4,
                    states: {
                        hover: {
                            lineWidth: 5
                        }
                    },
                    marker: {
                        enabled: false
                    },
                    pointStart: 1
                }
            },
            series: data
        });
    }

    createSpeciesChart() {
        let seriesCount = {};
        let generations = (<any>this.model).generationResults.slice(0, 10).reverse();
        for (const i in generations) {
            let generation = generations[i];
            for (let individual of generation.individuals) {
                let species = this.species.getSpecies(individual.chromossome);
                if (!seriesCount[species]) {
                    seriesCount[species] = [];
                }
                for (let j = seriesCount[species].length; j <= i; j++) {
                    seriesCount[species][j] = seriesCount[species][j] || 0
                }
                seriesCount[species][i] = seriesCount[species][i] + 1;
            }
        }
        
        let data = Object.keys(seriesCount).map((key) => {
            let data = seriesCount[key]; 
            for (let i = data.length; i < generations.length; i++) {
                data.push(0);
            }
            return {
                name: key,
                data: data
            }
        });


        this.speciesChart = Highcharts.chart('speciesChart', {
            chart: {
                type: 'areaspline',
                animation: Highcharts.svg,
                height: 150 
            },
            title: {
                text: ""
            },
            xAxis: {
                labels: {
                    overflow: 'justify'
                },
                tickInterval: 1
            },
            yAxis: {
                title: {
                    text: '# indivíduos'
                },
                max: (<any> this.model).populationSize,
                min: 0,
                minorGridLineWidth: 1,
                gridLineWidth: 1,
                alternateGridColor: null,
            },
            credits: {
                enabled: false,
            },
            legend: {
                enabled: false,
            },
            plotOptions: {
                areaspline: {
                    stacking: 'normal',
                    lineWidth: 0,
                    marker: {
                        enabled: false
                    },
                    pointStart: 1
                }
            },
            series: data
        });
    }

    runSingle() {
        this.model.send({ message: "runSingle" });
    }

    runForever() {
        this.model.send({ message: "runForever" });
    }

    interrupt() {
        this.model.send({ message: "interrupt" });
    }

}
