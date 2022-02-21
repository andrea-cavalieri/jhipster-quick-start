import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './foo.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const FooDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const fooEntity = useAppSelector(state => state.foo.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="fooDetailsHeading">
          <Translate contentKey="jhipsterApp.foo.detail.title">Foo</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{fooEntity.id}</dd>
          <dt>
            <span id="surname">
              <Translate contentKey="jhipsterApp.foo.surname">Surname</Translate>
            </span>
          </dt>
          <dd>{fooEntity.surname}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="jhipsterApp.foo.name">Name</Translate>
            </span>
          </dt>
          <dd>{fooEntity.name}</dd>
        </dl>
        <Button tag={Link} to="/foo" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/foo/${fooEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default FooDetail;
